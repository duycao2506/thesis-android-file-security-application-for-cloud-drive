

// progressBartransferHolder.hide();
// progressBarDecodeHolder.hide();
// fileDecodedNameTvHolder.hide();
// fileUploadNameTvHolder.hide();

$(function(){
    // var ulTransfer = $('#FormUpload .transferingList');
    // var ulDecode = $('#FormDecode .transferingList');

    // Initialize the jQuery File Upload plugin
    $('#FormUpload').fileupload({

        // This element will accept file drag/drop uploading
        dropZone: $('#profileForm1'),

        // This function is called when a file is added to the queue;
        // either via the browse button, or via drag/drop:
        add: addAndUploadFile,

        progress: progressing,
        done: finishing,

        fail:function(e, data){
            // Something has gone wrong!
        }

    });

    $('#FormDecode').fileupload({

        // This element will accept file drag/drop uploading
        dropZone: $('#profileForm2'),

        // This function is called when a file is added to the queue;
        // either via the browse button, or via drag/drop:
        add: addAndUploadFile,

        progress: progressing,

        done: finishingDecode,

        fail:function(e, data){
            // Something has gone wrong!
        }

    });


    // Prevent the default action when a file is dropped on the window
    $(document).on('profileForm1 dragover', function (e) {
        e.preventDefault();
    });

    $(document).on('profileForm2 dragover', function(e){
        e.preventDefault();
    });


    function  addAndUploadFile(e, data) {
        var tpl = $('<div  class="transferingItem"> <div style="padding-left: 1rem; padding-right: 1rem"><hr/></div> <p class="lblFilename"><span style="font-weight: bold">dsadsadsa</span> - 20 KB</p> <div class="progressBar"> <div class="progressHolder"> <div></div> </div> <p style="margin-left: 0rem; color: #00d039;text-align: left">Finish! </p> </div> </div>');
        var title = '<span style="font-weight: bold">' + data.files[0].name + '</span> - ' + formatFileSize(data.files[0].size);

        tpl.find('.lblFilename').html(title);
        tpl.find('.progressBar p').hide();
        tpl.find('.progressHolder div').width(0);
        tpl.find('.progressHolder').show();;
        data.context = tpl.appendTo($(e.target).find('.transferingList'));
        var jqXHR = data.submit();

    }

    function progressing(e, data){
        // Calculate the completion percentage of the upload
        var progress = parseInt(data.loaded / data.total * 100, 10);
        var progressPer = progress + "%";
        // Update the hidden input field and trigger a change
        // so that the jQuery knob plugin knows to update the dial
        data.context.find('.progressHolder div').width(progressPer);
    }

    function finishing(e, data){
        data.context.find('.progressHolder').hide();
        data.context.find('.progressBar p').show();
    }

    function finishingDecode(e, data){
        data.context.find('.progressHolder').hide();
        data.context.find('.progressBar p').html(data.result);
        data.context.find('.progressBar p').show();
    }

    // Helper function that formats the file sizes
    function formatFileSize(bytes) {
        if (typeof bytes !== 'number') {
            return '';
        }

        if (bytes >= 1000000000) {
            return (bytes / 1000000000).toFixed(2) + ' GB';
        }

        if (bytes >= 1000000) {
            return (bytes / 1000000).toFixed(2) + ' MB';
        }

        return (bytes / 1000).toFixed(2) + ' KB';
    }

});
