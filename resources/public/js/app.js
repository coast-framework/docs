$(function() {
  $(document).on("click", "[data-confirm]", function(e) {
    e.preventDefault();
    var el = e.target;
    var $this = $(this);

    if(confirm($this.data('confirm'))) {
      $this.closest('form').submit();
    }
  })

  $('a[href="' + window.location.pathname + '"]').each(function(el) {
    if(window.location.pathname === '/docs') { return; }

    $(this).css('color', '#357EDD');
  })
})
