var secondsleft = 0; 
var secondsleftIntervalId = null;

function displaySeconds( )
{
	$('secondsleftfield').update(secondsleft);
	secondsleft--;
	if (secondsleft < 0) clearInterval(secondsleftIntervalId);
}

function initializeIdleDisplay(idleInSeconds) {
	secondsleft = idleInSeconds;
	if (secondsleft > 0) secondsleftIntervalId = setInterval(displaySeconds, 1000);
}