module.exports = {
    app: 'src/main/webapp/',
    dist: 'target/www/',
    test: 'src/test/javascript/',
    scss: 'src/main/webapp/scss/',
    sassSrc: 'src/main/webapp/scss/**/*.{scss,sass}',
    sassVendor: 'src/main/webapp/scss/vendor.scss',
    cssDir: 'src/main/webapp/content/css',
    bower: 'src/main/webapp/bower_components/',
    tmp: 'target/tmp',
    revManifest: 'target/tmp/rev-manifest.json',
    port: 9000,
    apiPort: 80,
    liveReloadPort: 35729,
    uri: 'http://127.0.0.1:',
    constantTemplate:
        '(function () {\n' +
        '    \'use strict\';\n' +
        '    // DO NOT EDIT THIS FILE, EDIT THE GULP TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE\n' +
        '    angular\n' +
        '        .module(\'<%- moduleName %>\')\n' +
        '<% constants.forEach(function(constant) { %>        .constant(\'<%- constant.name %>\', <%= constant.value %>)\n<% }) %>;\n' +
        '})();\n'
};
