/*
 Simple linter, based on the Acorn [1] parser module

 All of the existing linters either cramp my style or have huge
 dependencies (Closure). So here's a very simple, non-invasive one
 that only spots

  - missing semicolons and trailing commas
  - variables or properties that are reserved words
  - assigning to a variable you didn't declare
  - access to non-whitelisted globals
    (use a '// declare global: foo, bar' comment to declare extra
    globals in a file)

 [1]: https://github.com/marijnh/acorn/
*/

var fs = require("fs"), acorn = require("acorn"), walk = require("acorn/dist/walk.js");

var getOptions = require("./options").getOptions;
var buildScopes = require("./scope").buildScopes;
var globals = require("./globals");
var loop = require("./loop");

var scopePasser = walk.make({
  Statement: function(node, prev, c) { c(node, node.scope || prev); },
  Function: function(node, _prev, c) { walk.base.Function(node, node.scope, c) }
});

function checkFile(fileName, options, text) {
  options = getOptions(options);
  if (text == null) text = fs.readFileSync(fileName, "utf8");

  var bad, msg;
  if (!options.trailing)
    bad = text.match(/[\t ]\n/);
  if (!bad && !options.tabs)
    bad = text.match(/\t/);
  if (!bad)
    bad = text.match(/[\x00-\x08\x0b\x0c\x0e-\x19\uFEFF]/);
  if (bad) {
    if (bad[0].indexOf("\n") > -1) msg = "Trailing whitespace";
    else if (bad[0] == "\t") msg = "Found tab character";
    else msg = "Undesirable character 0x" + bad[0].charCodeAt(0).toString(16);
    var info = acorn.getLineInfo(text, bad.index);
    fail(msg, {start: info, source: fileName});
  }

  if (options.blob && text.slice(0, options.blob.length) != options.blob)
    fail("Missing license blob", {source: fileName});

  try {
    var ast = acorn.parse(text, {
      locations: true,
      ecmaVersion: options.ecmaVersion,
      onInsertedSemicolon: options.semicolons !== true ? null : function(_, loc) {
        fail("Missing semicolon", {source: fileName, start: loc});
      },
      onTrailingComma: options.trailingCommas ? null : function(_, loc) {
        fail("Trailing comma", {source: fileName, start: loc});
      },
      forbidReserved: options.reservedProps ? false : "everywhere",
      sourceFile: fileName,
      sourceType: "module"
    });
  } catch (e) {
    fail(e.message, {source: fileName});
    return;
  }

  if (options.semicolons === false)
    require("./nosemicolons")(text, ast, fail)

  var scopes = buildScopes(ast, fail);

  var globalsSeen = Object.create(null);
  var ignoredGlobals = Object.create(null);

  var checkWalker = {
    UpdateExpression: function(node, scope) {assignToPattern(node.argument, scope);},
    AssignmentExpression: function(node, scope) {assignToPattern(node.left, scope);},
    Identifier: function(node, scope) {
      if (node.name == "arguments") return;
      readVariable(node, scope);
    },
    ExportNamedDeclaration: function(node, scope) {
      if (!node.source) for (var i = 0; i < node.specifiers.length; i++)
        readVariable(node.specifiers[i].local, scope);
      exportDecl(node.declaration, scope);
    },
    ExportDefaultDeclaration: function(node, scope) {
      exportDecl(node.declaration, scope);
    },
    FunctionExpression: function(node) {
      if (node.id && !options.namedFunctions) fail("Named function expression", node.loc);
    },
    ForStatement: function(node) {
      loop.checkReusedIndex(node, fail);
      if (node.test && node.update)
        loop.checkObviousInfiniteLoop(node.test, node.update, fail);
    },
    ForInStatement: function(node, scope) {
      assignToPattern(node.left.type == "VariableDeclaration" ? node.left.declarations[0].id : node.left, scope);
    },
    MemberExpression: function(node) {
      if (node.object.type == "Identifier" && node.object.name == "console" && !node.computed)
        fail("Found console." + node.property.name, node.loc);
    },
    DebuggerStatement: function(node) {
      fail("Found debugger statement", node.loc);
    }
  };

  function check(node, scope) {
    walk.simple(node, checkWalker, scopePasser, scope);
  }
  check(ast, scopes.top);

  function assignToPattern(node, scope) {
    walk.recursive(node, null, {
      Expression: function(node) {
        check(node, scope);
      },
      VariablePattern: function(node) {
        var found = searchScope(node.name, scope);
        if (found) {
          found.written = true;
        } else if (!(node.name in ignoredGlobals)) {
          ignoredGlobals[node.name] = true;
          fail("Assignment to global variable " + node.name, node.loc);
        }
      }
    }, null, "Pattern");
  }

  function readFromPattern(node, scope) {
    walk.recursive(node, null, {
      Expression: function() {},
      VariablePattern: function(node) { readVariable(node, scope); }
    }, null, "Pattern");
  }

  function readVariable(node, scope) {
    var found = searchScope(node.name, scope);
    if (found) {
      found.read = true;
      if (found.deadZone && node.start < found.node.start && sameFunction(scope, found.deadZone))
        fail(found.type.charAt(0).toUpperCase() + found.type.slice(1) + " used before its declaration", node.loc);
    } else {
      globalsSeen[node.name] = node.loc;
    }
  }

  function exportDecl(decl, scope) {
    if (!decl) return;
    if (decl.id) {
      readVariable(decl.id, scope);
    } else if (decl.declarations) {
      for (var i = 0; i < decl.declarations.length; i++)
        readFromPattern(decl.declarations[i].id, scope);
    }
  }

  function sameFunction(inner, outer) {
    for (;;) {
      if (inner == outer) return true;
      if (inner.type == "fn") return false;
      inner = inner.prev;
    }
  }

  function searchScope(name, scope) {
    for (var cur = scope; cur; cur = cur.prev)
      if (name in cur.vars) return cur.vars[name];
  }

  var allowedGlobals = Object.create(options.declareGlobals ? scopes.top.vars : null), m;
  if (options.allowedGlobals) options.allowedGlobals.forEach(function(v) { allowedGlobals[v] = true; });
  for (var i = 0; i < globals.jsGlobals.length; i++)
    allowedGlobals[globals.jsGlobals[i]] = true;
  if (options.browser)
    for (var i = 0; i < globals.browserGlobals.length; i++)
      allowedGlobals[globals.browserGlobals[i]] = true;

  if (m = text.match(/\/\/ declare global:\s+(.*)/))
    m[1].split(/,\s*/g).forEach(function(n) { allowedGlobals[n] = true; });
  for (var glob in globalsSeen)
    if (!(glob in allowedGlobals))
      fail("Access to global variable " + glob + ".", globalsSeen[glob]);

  for (var i = 0; i < scopes.all.length; ++i) {
    var scope = scopes.all[i];
    for (var name in scope.vars) {
      var info = scope.vars[name];
      if (!info.read) {
        if (info.type != "catch clause" && info.type != "function name" && name.charAt(0) != "_")
          fail("Unused " + info.type + " " + name, info.node.loc);
      } else if (!info.written) {
        fail(info.type.charAt(0).toUpperCase() + info.type.slice(1) + " " + name + " is never written to",
             info.node.loc);
      }
    }
  }

  function fail(msg, pos, end) {
    let loc = end ? pos.end : pos.start
    if (loc) msg += " (" + loc.line + ":" + loc.column + ")";
    if (options.message)
      options.message(pos.source, msg)
    else
      console["log"](pos.source + ": " + msg);
    failed = true;
  }
}

var failed = false;

function checkDir(dir, options) {
  fs.readdirSync(dir).forEach(function(file) {
    var fname = dir + "/" + file;
    if (/\.js$/.test(file)) checkFile(fname, options);
    else if (fs.lstatSync(fname).isDirectory()) checkDir(fname, options);
  });
}

exports.checkDir = checkDir;
exports.checkFile = checkFile;
exports.success = function() { return !failed; };
