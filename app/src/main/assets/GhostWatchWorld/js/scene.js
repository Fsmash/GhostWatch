$(document).ready(function() {
    var audioElement = document.createElement('audio');
    audioElement.setAttribute('src', 'assets/audio.mp3');

    $("#button").click(function(){
        $("#ray").toggle();
        audioElement.play();
    });
});