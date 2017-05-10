var defaultOptions = {
  ecmaVersion: 5,
  browser: false,
  tabs: false,
  trailing: false,
  semicolons: null,
  trailingCommas: false,
  reservedProps: false,
  namedFunctions: false,
  declareGlobals: true,
  allowedGlobals: null,
  blob: false,
  message: null
};

exports.getOptions = function(value) {
  var opts = {};
  if (value.autoSemicolons === false) value.semicolons = true;
  for (var prop in defaultOptions) {
    if (value && Object.prototype.hasOwnProperty.call(value, prop))
      opts[prop] = value[prop];
    else
      opts[prop] = defaultOptions[prop];
  }
  return opts;
};
