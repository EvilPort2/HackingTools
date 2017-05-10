var walk = require("acorn/dist/walk.js");

// Enforce (safe) semicolon-free style
//
// Complains about semicolons at end of statements, about statements
// that are dangerous in semicolon-free style not being prefixed with
// a semicolon, and tries to spot accidentally continued statements.

function needsLeadingSemicolon(text, pos) {
  var ch = text.charCodeAt(pos);
  if (ch == 40 || ch == 91) return true;
  if ((ch == 43 || ch == 45) && text.charCodeAt(pos + 1) != ch) return true;
  if (ch == 47) {
    var next = text.charCodeAt(pos + 1);
    if (next != 47 && next != 42) return true;
  }
  return false;
}

function col(text, pos) {
  var col = 0;
  while (pos && text.charCodeAt(pos - 1) != 10) { --pos; ++col; }
  return col;
}

function textAfter(node, text) {
  for (var pos = node.end; pos < text.length; pos++) {
    var ch = text.charCodeAt(pos);
    if (ch == 10) return false;
    if (ch == 47) {
      var after = text.charCodeAt(pos + 1);
      if (after == 47) return false;
      if (after == 42) {
        var end = text.indexOf("*/", pos + 2) + 2;
        var comment = text.slice(pos, end);
        if (comment.indexOf("\n") > -1) return false;
        pos = end - 1;
      }
    }
    if (ch != 32) return true;
  }
  return false;
}

module.exports = function(text, ast, fail) {
  walk.ancestor(ast, {
    Statement: function(node, parents) {
      var parent = parents[parents.length - 2];

      if (parent.init == this || parent.left == this ||
          /^(?:(?:For|ForIn|ForOf|While|Block|Empty|If|Labeled|With|Switch|Try)Statement|Program)$/.test(node.type))
        return; // A for or for/in init clause or a block

      if (text.charAt(node.end - 1) == ";" && !textAfter(node, text))
        fail("Semicolon found", node.loc, true);
      if (needsLeadingSemicolon(text, node.start) &&
          (parent.type == "BlockStatement" || parent.type == "Program") &&
          text.charAt(node.start - 1) != ";")
        fail("Missing leading semicolon", node.loc);

      var nextNewline = text.indexOf("\n", node.start);
      if (nextNewline < node.end) {
        var statementCol = col(text, node.start);
        while (nextNewline < node.end) {
          var firstNonWs = nextNewline + 1, startCol = 0;
          while (text.charCodeAt(firstNonWs + 1) == 32) { ++firstNonWs; ++startCol; }
          if (statementCol >= startCol && needsLeadingSemicolon(text, firstNonWs)) {
            fail("Possibly accidentally continued statement", node.loc);
            break;
          }
          nextNewline = text.indexOf("\n", nextNewline + 1);
          if (nextNewline < 0) break
        }
      }
    }
  });
};
