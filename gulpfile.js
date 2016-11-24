/*

* Install npm
* package.json contain all dependencies specified
@@ $ npm install
* Production file generation
@@ $ gulp --prod
* in dev mode just use:
@@ $ gulp

*/




// include gulp
var gulp = require('gulp');

// include plug-ins
var jshint = require('gulp-jshint'),
    stripDebug = require('gulp-strip-debug'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    autoprefix = require('gulp-autoprefixer'),
    minifyCSS = require('gulp-minify-css'),
    argv = require('yargs').argv,
    gulpif = require('gulp-if'),
    beautify = require('gulp-beautify'),
    del = require('del');


// Handy file paths also handler order of file compilation
paths = {
    css:  "./public/css/",
    js:   "./public/js/",
    supportCss: "./public/support/css/",
    supportJs: "./public/support/js/",
    commonJs: "./public/common/js/",
};

jsOrder = {
    bootstrap: paths.commonJs+"bootstrap.min.js",
    jquery: paths.supportJs+"jquery-1.12.0.min.js",
    jqDt: paths.supportJs+"jquery.dataTables.min.js",
    npProgress: paths.supportJs+"nprogress.js",
    tokenInput: paths.supportJs+"jquery.tokeninput.js",
    btnDt: paths.supportJs+"btnExport/dataTables.buttons.min.js",
    btnFlash: paths.supportJs+"btnExport/buttons.flash.min.js",
    jsZip: paths.supportJs+"btnExport/jszip.min.js",
    vfsFonts: paths.supportJs+"btnExport/vfs_fonts.js",
    btnHtml5: paths.supportJs+"btnExport/buttons.html5.min.js",
    momentJs: paths.supportJs+"moment-2.8.4.min.js",
    datetimeMomentJs: paths.supportJs+"datetime-moment.js",
    jqueryUi: paths.supportJs+"jquery-ui.js",
    bsNotify: paths.supportJs+"bootstrap-notify.min.js",
    searchController: paths.supportJs+"searchController.js",
    workFlowController: paths.supportJs+"workFlowController.js",
    preScreenCandidate: paths.commonJs+"pre_screen_candidate.js"
};

cssOrder = {
    bootstrap: paths.supportCss+"bootstrap.min.css",
    dtBootstrap: paths.supportCss+"dataTables.bootstrap.min.css",
    jqDt: paths.supportCss+"jquery.dataTables.min.css",
    search: paths.supportCss+"search.css",
    npProgress: paths.supportCss+"nprogress.css",
    tokenFb: paths.supportCss+"token-input-facebook.css",
    btnDt: paths.supportCss+"buttons.dataTables.min.css",
    jqueryUi: paths.supportCss+"jquery-ui.css"
};

// Clean task
gulp.task('clean', function () {
    return del([
        './public/build/support/*'
        // here we use a globbing pattern to match everything inside the `mobile` folder
        //'dist/mobile/**/*',
        // we don't want to clean this file though so we negate the pattern
        //'!dist/mobile/deploy.json'
    ]);
});


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
    gulp.src([jsOrder.bootstrap, jsOrder.jquery, jsOrder.jqDt, jsOrder.npProgress, jsOrder.tokenInput, jsOrder.btnDt, jsOrder.btnFlash, jsOrder.jsZip, jsOrder.vfsFonts, jsOrder.btnHtml5, jsOrder.momentJs, jsOrder.datetimeMomentJs, jsOrder.jqueryUi, jsOrder.bsNotify])
        .pipe(concat('sapp.min.js'))
        .pipe(gulpif(argv.prod, uglify(), beautify()))
        .pipe(gulpif(argv.prod, stripDebug()))
        .pipe(gulp.dest('./public/build/support/'));
});

// style minify
gulp.task('supportStyles', function() {
    gulp.src([cssOrder.bootstrap, cssOrder.dtBootstrap, cssOrder.jqDt, cssOrder.search, cssOrder.npProgress, cssOrder.tokenFb, cssOrder.btnDt, cssOrder.jqueryUi])
        .pipe(concat('sapp.min.css'))
        .pipe(gulpif(argv.prod, minifyCSS()))
        .pipe(gulp.dest('./public/build/support/'));
});

// JS concat, strip debugging and minify for datatable bundle
gulp.task('datatableBundleScript', function() {
    gulp.src([jsOrder.jqDt, jsOrder.btnDt, jsOrder.btnFlash, jsOrder.jsZip, jsOrder.vfsFonts, jsOrder.btnHtml5])
        .pipe(concat('datatableBundle.min.js'))
        .pipe(gulpif(argv.prod, uglify(), beautify()))
        .pipe(gulpif(argv.prod, stripDebug()))
        .pipe(gulp.dest('./public/build/support/'));
});

// datatable style minify
gulp.task('datatableBundleStyle', function() {
    gulp.src([cssOrder.dtBootstrap, cssOrder.jqDt, cssOrder.btnDt])
        .pipe(concat('datatableBundle.min.css'))
        .pipe(gulpif(argv.prod, minifyCSS()))
        .pipe(gulp.dest('./public/build/support/'));
});

// individual JS minify
gulp.task('searchControllerScript', function() {
    gulp.src([jsOrder.searchController])
        .pipe(concat('searchController.min.js'))
        .pipe(gulpif(argv.prod, uglify(), beautify()))
        .pipe(gulpif(argv.prod, stripDebug()))
        .pipe(gulp.dest('./public/build/support/'));
});
// individual JS minify
gulp.task('workFlowControllerScript', function() {
    gulp.src([jsOrder.workFlowController])
        .pipe(concat('workFlowController.min.js'))
        .pipe(gulpif(argv.prod, uglify(), beautify()))
        .pipe(gulpif(argv.prod, stripDebug()))
        .pipe(gulp.dest('./public/build/support/'));
});
// individual JS minify
gulp.task('preScreenCandidateScript', function() {
    gulp.src([jsOrder.preScreenCandidate])
        .pipe(concat('pre_screen_candidate.min.js'))
        .pipe(gulpif(argv.prod, uglify(), beautify()))
        .pipe(gulpif(argv.prod, stripDebug()))
        .pipe(gulp.dest('./public/build/scripts/'));
});

// default gulp task
gulp.task('default', ['clean', 'scripts', 'styles', 'supportScripts', 'supportStyles', 'datatableBundleScript',
    'datatableBundleStyle', 'searchControllerScript', 'workFlowControllerScript', 'preScreenCandidateScript'], function() {
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
    // watch for datatable bundle css changes
    gulp.watch(paths.supportJs+'*.css', function() {
        gulp.run('datatableBundleStyles');
    });
    // watch for datatable bundle js changes
    gulp.watch(paths.supportJs+'*.js', function() {
        gulp.run('datatableBundleScript');
    });
    // watch for searchController solo js changes
    gulp.watch(paths.supportJs+'*.js', function() {
        gulp.run('searchControllerScript');
    });
    // watch for workFlowControllerScript solo js changes
    gulp.watch(paths.supportJs+'*.js', function() {
        gulp.run('workFlowControllerScript');
    });
    // watch for preScreenCandidateScript solo js changes
    gulp.watch(paths.supportJs+'*.js', function() {
        gulp.run('preScreenCandidateScript');
    });
});


require('es6-promise').polyfill();

// Handle the error
function errorHandler (error) {
    console.log(error.toString());
    this.emit('end');
}

