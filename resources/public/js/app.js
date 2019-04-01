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
    if(window.location.pathname === '/docs') {
      $('a[href="/docs/installation.md"]').css('color', '#357EDD');
    } else if (window.location.pathname.startsWith('/docs')) {
      $(this).css('color', '#357EDD');
    }
  })

  $(document).on('click', '#edit', function(e) {
    $('#form-container').show();
    $('#preview-container').hide();
    $(this).addClass('blue').removeClass('gray')
    $('#preview').removeClass('blue').addClass('gray')
  })

  $(document).on('click', '#preview', function(e) {
    e.preventDefault();

    $('#form-container').hide();
    $(this).addClass('blue').removeClass('gray')
    $('#edit').removeClass('blue').addClass('gray')

    data = {
      title: $('[name="post/title"]').val(),
      body: $('[name="post/body"]').val()
    }
    data['__anti-forgery-token'] = $('[name="__anti-forgery-token"]').val()

    $.ajax({
      url: "/posts/preview",
      data: data,
      type: "POST",
      dataType: "json",
      complete: function(res) {
        $("#preview-container").html(res.responseText).show()
        document.querySelectorAll('pre code').forEach(function(block) {
          hljs.highlightBlock(block);
        });
      }
    });
  })

  var timer = null;
  var saveCount = 0;
  $(document).on('keyup', '[name="post/title"],[name="post/body"]', onKeyUp);

  function onKeyUp(e) {
    var $form = $(this).closest('form');

    if (!!timer) { return; }

    timer = setTimeout(function() {
      $('#status').text('Saving...')

      $.ajax({
        url: $form.attr('action'),
        type: $form.attr('method'),
        data: $form.serialize(),
        dataType: 'json',
        success: function(data) {
          timer = null
          $('#status').text('Saved')

          var url = data.url
          if(saveCount === 0 && !!url) {
            history.pushState({}, '', url);
          }

          saveCount++;

          setTimeout(function() {
            $('#status').html('&nbsp;')
          }, 2000);

          if (!!data['form-params']) {
            $form.attr('action', data['form-params']['action'])
            $form.attr('method', data['form-params']['method'])
            $form.append('<input type="hidden" name="_method" value="' + data['form-params']['_method'] + '" />')
          }
        }
      });
    }, 5000);
  }
})
