var walk = require("acorn/dist/walk.js");

exports.buildScopes = function(ast, fail) {
  var scopes = [];

  function makeScope(prev, type) {
    var scope = {vars: Object.create(null), prev: prev, type: type};
    scopes.push(scope);
    return scope;
  }
  function fnScope(scope) {
    while (scope.type != "fn") scope = scope.prev;
    return scope;
  }
  function addVar(scope, name, type, node, deadZone, written) {
    if (deadZone && (name in scope.vars))
      fail("Duplicate definition of " + name, node.loc);
    scope.vars[name] = {type: type, node: node, deadZone: deadZone && scope,
                        written: written, read: false};
  }

  function makeCx(scope, binding) {
    return {scope: scope, binding: binding};
  }

  function isBlockScopedDecl(node) {
    return node.type == "VariableDeclaration" && node.kind != "var";
  }

  var topScope = makeScope(null, "fn");

  walk.recursive(ast, makeCx(topScope), {
    Function: function(node, cx, c) {
      var inner = node.scope = node.body.scope = makeScope(cx.scope, "fn");
      var innerCx = makeCx(inner, {scope: inner, type: "argument", deadZone: true, written: true});
      for (var i = 0; i < node.params.length; ++i)
        c(node.params[i], innerCx, "Pattern");

      if (node.id) {
        var decl = node.type == "FunctionDeclaration";
        addVar(decl ? cx.scope : inner, node.id.name,
               decl ? "function" : "function name", node.id, false, true);
      }
      c(node.body, innerCx, node.expression ? "ScopeExpression" : "ScopeBody")
    },
    TryStatement: function(node, cx, c) {
      c(node.block, cx, "Statement");
      if (node.handler) {
        var inner = node.handler.body.scope = makeScope(cx.scope, "block");
        addVar(inner, node.handler.param.name, "catch clause", node.handler.param, false, true);
        c(node.handler.body, makeCx(inner), "ScopeBody");
      }
      if (node.finalizer) c(node.finalizer, cx, "Statement");
    },
    Class: function(node, cx, c) {
      if (node.id && node.type == "ClassDeclaration")
        addVar(cx.scope, node.id.name, "class name", node, true, true);
      if (node.superClass) c(node.superClass, cx, "Expression");
      for (var i = 0; i < node.body.body.length; i++)
        c(node.body.body[i], cx);
    },
    ImportDeclaration: function(node, cx) {
      for (var i = 0; i < node.specifiers.length; i++) {
        var spec = node.specifiers[i].local
        addVar(cx.scope, spec.name, "import", spec, false, true)
      }
    },
    Expression: function(node, cx, c) {
      if (cx.binding) cx = makeCx(cx.scope)
      c(node, cx);
    },
    VariableDeclaration: function(node, cx, c) {
      for (var i = 0; i < node.declarations.length; ++i) {
        var decl = node.declarations[i];
        c(decl.id, makeCx(cx.scope, {
          scope: node.kind == "var" ? fnScope(cx.scope) : cx.scope,
          type: node.kind == "const" ? "constant" : "variable",
          deadZone: node.kind != "var",
          written: !!decl.init
        }), "Pattern");
        if (decl.init) c(decl.init, cx, "Expression");
      }
    },
    VariablePattern: function(node, cx) {
      var b = cx.binding;
      if (b) addVar(b.scope, node.name, b.type, node, b.deadZone, b.written);
    },
    BlockStatement: function(node, cx, c) {
      if (!node.scope && node.body.some(isBlockScopedDecl)) {
        node.scope = makeScope(cx.scope, "block");
        cx = makeCx(node.scope)
      }
      walk.base.BlockStatement(node, cx, c);
    },
    ForInStatement: function(node, cx, c) {
      if (!node.scope && isBlockScopedDecl(node.left)) {
        node.scope = node.body.scope = makeScope(cx.scope, "block");
        cx = makeCx(node.scope);
      }
      walk.base.ForInStatement(node, cx, c);
    },
    ForStatement: function(node, cx, c) {
      if (!node.scope && node.init && isBlockScopedDecl(node.init)) {
        node.scope = node.body.scope = makeScope(cx.scope, "block");
        cx = makeCx(node.scope);
      }
      walk.base.ForStatement(node, cx, c);
    }
  }, null);

  return {all: scopes, top: topScope};
};
