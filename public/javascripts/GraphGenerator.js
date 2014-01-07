/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 06/01/14
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */

function getCoordinateNumberPerNode() {
    return $("input[name=coordinatePerNode]:checked","#coordinateParamForm").val();
}

function showError(errorToShow){
    $("#notifications").append('<div data-alert class="alert-box warning">'+errorToShow+'<a href="#" class="close">&times;</a> </div>');
    $("#notifications").foundation();
}

function handleCooordinate (data){

}

function getCoordinateList(networkId){
    $.ajax({
        url: "http://"+window.location.hostname+"/networks/"+networkId+"/coordinates",
        data: {"limit": getCoordinateNumberPerNode()},
        accepts: "application/json",
        error: showError(e),
        success: handleCooordinate(data)
    });
}