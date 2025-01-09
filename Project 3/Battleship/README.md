Project Contributors: Ashiqul Alam, Michele Lobato, Cason Vela, Jason Sullivan

No extra specific setup. Should work with normal javaFX vm argument

BUGS YOU MAY ENCOUNTER:

BUG#1: Sound Effect Duplicate/Doesn't play if you're spam clicking
Whenever you click on a square it's supposed to play the (water drop / explosion boom) sound effect. Sometimes it will play "two" sounds or play them both really fast or play neither of them at all.
This was difficult to fix because the code to play the sound effect is in out square object and the enemy move code occurs once the players turn is over. So if the players turn ends it plays the effect, but will then also instantaneously play the effect for the enemy move. So basically sometimes it plays two sound effects in a row. If you click slowly and take your time through the game it shouldn't happen much

BUG#2: Sometimes background music just randomly stops
I don't know why it does this actually, because it doesn't difinitively stop everytime X happens. This was difficult to fix because I think it could be an issue with our mediaPlayer being able to play so many different mp3s instantaneously, but since we couldn't pinpoint the root cause we had to leave it that way.



