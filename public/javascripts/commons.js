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

function showError(status,errorToShow){
    $("#notifications").append('<div data-alert class="alert-box warning">'+status+" :"+errorToShow+'<a href="#" class="close">&times;</a> </div>');
}

function nullArray(length){
    var nullRow = [null];
    for (var i = 0 ; i<length ; i ++ ){
        nullRow.push(null);
    }
    return nullRow;
}