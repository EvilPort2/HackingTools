# Blint

Simple JavaScript linter.

```javascript
var blint = require("blint");
blint.checkFile("foo.js");
blint.checkDir("src");
```

When the linter encounters problems, it will write something to
stdout, and set a flag, which you can retrieve with `blint.success()`.

```javascript
process.exit(blint.success() ? 0 : 1);
```

Both `checkFile` and `checkDir` take a second optional options
argument. These are the defaults:

```javascript
var defaultOptions = {
  // Version of the language to parse
  ecmaVersion: 5,
  // Whitelist globals exported by the browser
  browser: false,
  // Allow tabs
  tabs: false,
  // Allow trailing whitespace
  trailing: false,
  // True to require semicolons, false to disallow them
  semicolons: null,
  // Allow trailing commas
  trailingCommas: false,
  // Allow unquoted properties that are reserved words
  reservedProps: false,
  // Allow the code to declare top-level variables
  declareGlobals: true
};
```

Released under an MIT license.
