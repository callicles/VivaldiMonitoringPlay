/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 06/01/14
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
google.load("visualization", "1", {packages: ["corechart"]});

function getSelectedId(object){
    return $("select[name="+object+"]").find(":selected").val();
}

function showError(content, status,errorToShow){
    $("#notifications").append('<div data-alert class="alert-box warning">'+status+" :"+errorToShow+' - '+content+'<a href="#" class="close">&times;</a> </div>');
    $(document).foundation();
}
function showSuccess(successText){
    $("#notifications").append('<div data-alert class="alert-box success">'+successText+'<a href="#" class="close">&times;</a> </div>');
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
            showError(jqXHR , textStatus, errorThrown);
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
    fetchNetworkList(updateNetworkList);
}

function updateNetworkList(data){
    var networkList = $("select[name=network]");
    var selectedNetwork = getSelectedId("network");

    networkList.empty()
    for (var i = 0 ; i < data.length ; i++){
        networkList.append('<option value="'+data[i].id+'">'+data[i].networkName+'</option>');
    }
    networkList.find("[value="+selectedNetwork+"]").first().prop("selected",true);
}

function fetchNetworkList(callback){
    $.ajax({
        url: location.protocol+"//"+window.location.host+"/networks/",
        accepts: "application/json",
        error: function( jqXHR, textStatus, errorThrown ){
            showError(jqXHR,textStatus, errorThrown);
        },
        success:  function( data ){
            callback(data)
        }
    });
}

function deleteNetworks(networks){
    var results= [];

    for (var i = 1 ; i<networks.length ; i++){
        results.push(
            $.ajax({
                type: "DELETE",
                url: location.protocol+"//"+window.location.host+"/networks/"+networks[i].id,
                accepts: "application/json",
                error: function( jqXHR, textStatus, errorThrown ){
                    showError(jqXHR,textStatus, errorThrown);
                }
            })
        );
    }

    $.when.apply(this, results).done(function() {

        var success = true;
        for(var i=0;i<arguments.length;i++){
            if(arguments[i][1] != "success"){
                success = false;
            }
            if(arguments[1] == "success"){
                success=true;
            }
        }

        if (success){
            fetchNetworkList(updateNetworkList);
            showSuccess('Database cleaned up !');
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


