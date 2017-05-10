var fs = require("fs"), path = require("path");

var blint = require("../blint");

var filter = process.argv[2];

var failed = 0;

var casePath = path.resolve(__dirname, "cases");
fs.readdirSync(casePath).forEach(function(file) {
  if (!file.match(/\.js$/) || filter && file.indexOf(filter) != 0) return;

  var text = fs.readFileSync(path.resolve(casePath, file), "utf8");
  var declaredOptions = /\/\/ options=(.*)/.exec(text);
  var options = declaredOptions ? JSON.parse(declaredOptions[1]) : {};
  if (!options.ecmaVersion) options.ecmaVersion = 6;
  var declaredOutput = /\/\/ ---\n([^]+)/.exec(text);
  var expected = declaredOutput ? declaredOutput[1].trim().split("\n").map(function(line) { return /^\s*\/\/\s*(.*)/.exec(line)[1]; }) : [];

  var result = [];
  options.message = function(_file, msg) { result.push(msg); };
  blint.checkFile(file, options, text);

  for (var i = 0; i < result.length; i++) {
    var found = expected.indexOf(result[i]);
    if (found > -1) {
      result.splice(i--, 1);
      expected.splice(found, 1);
    }
  }

  if (expected.length || expected.length) {
    if (failed) console.log("");
    failed++;
  }
  if (expected.length)
    console.log(file + ": Missing messages\n  " + expected.join("\n  "));
  if (result.length)
    console.log(file + ": Unexpected messages\n  " + result.join("\n  "));
});

process.exit(failed);
