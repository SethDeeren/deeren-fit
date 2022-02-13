var theDate;
var challengeId;

$(document).ready(function(){
    theDate = setDate();
    challengeId = parseInt(window.location.pathname.substr(12,1));

    flatpickr("input[type=datetime-local]",{
        defaultDate: theDate,
        onChange: function(selectedDate, date, instance){loadTrackers(date)} // this is changing theDate to a string
    });


    $('#reps-button').click(function(){        
       var reps = parseInt($('#reps').val());
            if(isNaN(reps)){
                alert("Must enter a number for reps")
            }
            else if(reps <= 0){
                alert("Reps must be greater then 0")
            } else {
                postSet(reps);
            }
    });

    $('#reps').keypress(function(event){
        if(event.which == 13){
            var reps = parseInt($('#reps').val());
            if(isNaN(reps)){
                alert("Must enter a number for reps")
            }
            else if(reps <= 0){
                alert("Reps must be greater then 0")
            } else {
                postSet(reps);
            }
            
        }    
    });
});


function postSet(reps) {


    // send post request
//    var stringDate = theDate;
//    stringDate = stringDate.toISOString().split('T')[0];//----------------------------------------------will somtimes be date and somtimes string

    var stringDate = $('#date').val();

    console.log("The of the date is " + typeof(theDate));
    console.log(theDate);
   
    $.ajax({
        type: "POST",
        url: "/api/my_stats",
        contentType: "application/json; charset=utf-8",
        data: `{"day": "${stringDate}", "reps": ${reps}, "challengeId": ${challengeId}}`,
        success: function(data){
            console.log(data);
            loadTrackers(stringDate);
            $('#reps').val('');
        }
    });       
};

function deleteTracker(identifier){
    let id = $(identifier).data('tracker_id');

    var stringDate = $('#date').val();

    $.ajax({
        type: "DELETE",
        url: `/api/my_stats/${id}`,
        //contentType: "application/json; charset=utf-8",
        //data: `{"day": "${stringDate}", "reps": ${reps}, "challengeId": ${challengeId}}`,
        success: function(data){
            console.log(data);
            loadTrackers(stringDate);
        }
    });
}


async function loadTrackers(date){
    console.log("type of the date being passed to load trackers is " + typeof(date));
    var domDate = new Date($('#date').val());//----------------------------------------------------------- will need this more often
    console.log("type of the date being pulled from the dom " + typeof(domDate));
    console.log(domDate);
    // flat pickr changes date to string this changes it back to date object
   // theDate = domDate;
    $('#theTable').find('tbody').detach();
    $('#theTable').append($('<tbody>'));
    console.log("the date is " + theDate);
    let response = await $.getJSON(`/api/my_stats?date=${date}&challengeId=${challengeId}`);
    for(let i = 0; i < response.length; i++) {
        $("#theTable tbody").append(
            `<tr>
                <td>${i + 1}</td>
                <td>${response[i].reps}</td>
                <td>
                <div>
                    <button data-tracker_id="${response[i].id}"
                            id="delete-button" class="btn btn-outline-danger"
                            onclick="deleteTracker(this);"
                    >Delete</button>
                </div>
                </td>
            </tr>`)
    }    
    console.log(response)
};

function setDate(){
    let theReturnDate;

    if(window.location.pathname.substr(26) != ''){
        theReturnDate = new Date(window.location.pathname.substr(26));
        //theReturnDate.setUTCDate(theReturnDate.getUTCDate() + 1);
    } else {
        theReturnDate = new Date();
    }

    return theReturnDate;
};
