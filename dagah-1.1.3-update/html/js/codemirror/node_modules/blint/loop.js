var walk = require("acorn/dist/walk.js");

exports.checkReusedIndex = function(node, fail) {
  if (!node.init || node.init.type != "VariableDeclaration") return;
  var name = node.init.declarations[0].id.name;
  walk.recursive(node.body, null, {
    Function: function() {},
    VariableDeclaration: function(node, st, c) {
      for (var i = 0; i < node.declarations.length; i++)
        if (node.declarations[i].id.name == name)
          fail("Redefined loop variable", node.declarations[i].id.loc);
      walk.base.VariableDeclaration(node, st, c);
    }
  });
};

exports.checkObviousInfiniteLoop = function(test, update, fail) {
  var vars = Object.create(null);
  function opDir(op) {
    if (/[<+]/.test(op)) return 1;
    if (/[->]/.test(op)) return -1;
    return 0;
  }
  function store(name, dir) {
    if (!(name in vars)) vars[name] = {below: false, above: false};
    if (dir > 0) vars[name].up = true;
    if (dir < 0) vars[name].down = true;
  }
  function check(node, dir) {
    var known = vars[node.name];
    if (!known) return;
    if (dir > 0 && known.down && !known.up ||
        dir < 0 && known.up && !known.down)
      fail("Suspiciously infinite-looking loop", node.loc);
  }
  walk.simple(test, {
    BinaryExpression: function(node) {
      if (node.left.type == "Identifier")
        store(node.left.name, opDir(node.operator));
      if (node.right.type == "Identifier")
        store(node.right.name, -opDir(node.operator));
    }
  });
  walk.simple(update, {
    UpdateExpression: function(node) {
      if (node.argument.type == "Identifier")
        check(node.argument, opDir(node.operator));
    },
    AssignmentExpression: function(node) {
      if (node.left.type == "Identifier") {
        if (node.operator == "=" && node.right.type == "BinaryExpression" && node.right.left.name == node.left.name)
          check(node.left, opDir(node.right.operator));
        else
          check(node.left, opDir(node.operator));
      }
    }
  });
};
