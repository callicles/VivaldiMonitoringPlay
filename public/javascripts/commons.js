/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 06/01/14
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
google.load("visualization", "1", {packages: ["corechart"]});

var automaticUpdate;

function getSelectedId(object){
    return $("select[name="+object+"]").find(":selected").val();
}

function showError(content, status,errorToShow){
    $("#notifications").append('<div data-alert class="alert-box warning">'+status+" :"+errorToShow+' - '+content+'<a href="#" class="close">&times;</a> </div>');
    $(document).foundation();
}

function nullArray(length){
    var nullRow = [null];
    for (var i = 0 ; i<length ; i ++ ){
        nullRow.push(null);
    }
    return nullRow;
}

function createNetwork(){
    var networkName =  $("#newNetworkName").val()
    $.ajax({
        type: "POST",
        url: location.protocol+"//"+window.location.host+"/networks/",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({"networkName": networkName}),
        error: function( jqXHR, textStatus, errorThrown ){
            showError(jqXHR.responseText , textStatus, errorThrown);
        },
        success:  function(data){
            showNetworkData(data);
        }
    });
}

function showNetworkData(data){
    var modal = $("#addNetworkModalContent");
    modal.before('<div data-alert class="alert-box success radius">Network created : {id:'+data.id+', networkName: '+data.networkName+'}<a href="#" class="close">&times;</a></div>');
    $(document).foundation();
    updateNetworkList();
}

function updateNetworkList(){
    var networkList = $("select[name=network]");
    var selectedNetwork = getSelectedId("network");

    $.ajax({
        url: location.protocol+"//"+window.location.host+"/networks/",
        accepts: "application/json",
        error: function( jqXHR, textStatus, errorThrown ){
            showError(textStatus, errorThrown);
        },
        success:  function( data ){
            networkList.empty()
            for (var i = 0 ; i < data.length ; i++){
                networkList.append('<option value="'+data[i].id+'">'+data[i].networkName+'</option>');
            }
            networkList.find("[value="+selectedNetwork+"]").first().prop("selected",true);
        }
    });
}

var automaticUpdate;

function toggleRefresh(toExecute){
    var $icon = $( "#fi-refresh" ),
        animateClass = "icon-refresh-animate",
        $button = $('#refreshButton'),
        $addNetworkButton = $('#addNetworkButton');

    if($icon.hasClass(animateClass)){
        $icon.removeClass( animateClass );
        $button.removeClass( "success" );
        $addNetworkButton.removeAttr("style");
        $addNetworkButton.prop('disabled', false);
        clearInterval(automaticUpdate);

    }else {
        $icon.addClass( animateClass );
        $button.addClass( "success" );
        $addNetworkButton.prop('disabled', true);
        $addNetworkButton.css("background","#b7b7b7");
        automaticUpdate = setInterval(toExecute,1000);
    }
    $(document).foundation();
}


