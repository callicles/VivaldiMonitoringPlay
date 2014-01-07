/**
 * Created with IntelliJ IDEA.
 * User: Nicolas
 * Date: 06/01/14
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
google.load("visualization", "1", {packages: ["corechart"]});

function getCoordinateNumberPerNode() {
    return $("input[name=coordinatePerNode]:checked","#coordinateParamForm").val();
}

function showError(status,errorToShow){
    $("#notifications").append('<div data-alert class="alert-box warning">'+status+" :"+errorToShow+'<a href="#" class="close">&times;</a> </div>');
}

function getCoordinateList(networkId){
    $.ajax({
        url: "http://"+window.location.host+"/networks/"+networkId+"/coordinates",
        data: {"limit": getCoordinateNumberPerNode()},
        accepts: "application/json",
        error: function( jqXHR, textStatus, errorThrown ){
            showError(textStatus, errorThrown);
        },
        success:  function( data ){
            handleCoordinate(data);
        }
    });
}

function nullArray(length){
    var nullRow = [null];
    for (var i = 0 ; i<length ; i ++ ){
        nullRow.push(null);
    }
    return nullRow;
}

function handleCoordinate(data) {

    var matrix = [["x"]];
    var newRow = [];

    for (var i = 0 ; i<data.length ; i ++ ){
        matrix[0].push("node : "+data[i][0].nodeId);
    }

    for (var i = 0 ; i<data.length ; i++){
        for (var j = 0 ; j<data[i].length ; j ++){
            newRow = nullArray(data.length);
            newRow[0] = data[i][j].x;
            newRow[i+1] = data[i][j].y;
            matrix.push(newRow);
        }
    }

    var dataToPrint = new google.visualization.arrayToDataTable(matrix);


    var options = {
        title: 'Nodes Coordinates',
        hAxis: {title: 'x', minValue: 0, maxValue: 15},
        vAxis: {title: 'y', minValue: 0, maxValue: 15}
    };

    var chart = new google.visualization.ScatterChart(document.getElementById('chart_div'));
    chart.draw(dataToPrint, options);
}