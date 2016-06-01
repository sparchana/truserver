// include gulp
var gulp = require('gulp');

// include plug-ins
var jshint = require('gulp-jshint');
var stripDebug = require('gulp-strip-debug');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var autoprefix = require('gulp-autoprefixer');
var minifyCSS = require('gulp-minify-css');

// Handy file paths also handler order of file compilation
paths = {
    css:  "./public/css/",
    js:   "./public/js/",
    supportCss: "./public/support/css/",
    supportJs: "./public/support/js/"
};

jsOrder = {
    bootstrap: paths.supportJs+"bootstrap.min.js",
    jquery: paths.supportJs+"jquery-1.12.0.min.js",
    jqDt: paths.supportJs+"jquery.dataTables.min.js",
    npProgress: paths.supportJs+"nprogress.js",
    tokenInput: paths.supportJs+"jquery.tokeninput.js",
    btnDt: paths.supportJs+"btnExport/dataTables.buttons.min.js",
    btnFlash: paths.supportJs+"btnExport/buttons.flash.min.js",
    jsZip: paths.supportJs+"btnExport/jszip.min.js",
    pdfMake: paths.supportJs+"btnExport/pdfmake.min.js",
    vfsFonts: paths.supportJs+"btnExport/vfs_fonts.js",
    btnHtml5: paths.supportJs+"btnExport/buttons.html5.min.js",
    searchController: paths.supportJs+"searchController.js",
};

cssOrder = {
    bootstrap: paths.supportCss+"bootstrap.min.css",
    dtBootstrap: paths.supportCss+"dataTables.bootstrap.min.css",
    jqDt: paths.supportCss+"jquery.dataTables.min.css",
    search: paths.supportCss+"search.css",
    npProgress: paths.supportCss+"nprogress.css",
    tokenFb: paths.supportCss+"token-input-facebook.css",
    btnDt: paths.supportCss+"buttons.dataTables.min.css"
};


// JS hint task
gulp.task('jshint', function() {
    gulp.src(paths.js + '*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

// JS concat, strip debugging and minify
gulp.task('scripts', function() {
    gulp.src([paths.js + 'scroll.js',paths.js+'*.js'])
        .pipe(uglify()).on('error', errorHandler)
        .pipe(concat('app.min.js')).on('error', errorHandler)
        .pipe(gulp.dest('./public/build/scripts/'));
});

// style minify
gulp.task('styles', function() {
    gulp.src(paths.css+'*.css')
        .pipe(concat('app.min.css'))
        .pipe(minifyCSS())
        .pipe(gulp.dest('./public/build/styles/'));
});

// JS concat, strip debugging and minify
gulp.task('supportScripts', function() {
    gulp.src([jsOrder.bootstrap, jsOrder.jquery, jsOrder.jqDt, jsOrder.npProgress, jsOrder.tokenInput, jsOrder.btnDt, jsOrder.btnFlash, jsOrder.jsZip, jsOrder.pdfMake, jsOrder.vfsFonts, jsOrder.btnHtml5, jsOrder.searchController])
        .pipe(uglify())
        .pipe(concat('sapp.min.js'))
        .pipe(stripDebug())
        .pipe(gulp.dest('./public/build/support/'));
});

// style minify
gulp.task('supportStyles', function() {
    gulp.src([cssOrder.bootstrap, cssOrder.dtBootstrap, cssOrder.jqDt, cssOrder.search, cssOrder.npProgress, cssOrder.tokenFb, cssOrder.btnDt])
        .pipe(concat('sapp.min.css'))
        .pipe(minifyCSS())
        .pipe(gulp.dest('./public/build/support/'));
});

// default gulp task
gulp.task('default', ['scripts', 'styles', 'supportScripts', 'supportStyles'], function() {
    // watch for CSS changes
    gulp.watch(paths.css+'*.css', function() {
        gulp.run('styles');
    });
    // watch for js changes
    gulp.watch(paths.css+'*.css', function() {
        gulp.run('scripts');
    });
    // watch for support CSS changes
    gulp.watch(paths.supportCss+'*.css', function() {
        gulp.run('supportStyles');
    });
    // watch for support js changes
    gulp.watch(paths.supportJs+'*.js', function() {
        gulp.run('supportScripts');
    });
});


require('es6-promise').polyfill();

// Handle the error
function errorHandler (error) {
    console.log(error.toString());
    this.emit('end');
}

