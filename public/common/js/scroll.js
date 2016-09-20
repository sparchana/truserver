jQuery.noConflict();
  jQuery(document).ready(function($) {
    $('[data-anchor]').click(function() {
      var target = $($(this).data('anchor'));
      if (target.length) {
        $('html, body').animate({
        scrollTop: target.offset().top
        }, 1000);
      }
  });
});