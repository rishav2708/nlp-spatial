import sys
import json
d={}
l=['enjoy', 'friends', 'dating', 'sleep', 'relax', 'family']
for i in l:
	d[i]={}
	
d['enjoy']={'explaination':"""Sir... I think you have choosen to enjoy a lot. I would suggest you with some movie spots and 
                        pubs where you can enjoy to get high and some quality free time at places where there would be 
                        less population. Enjoy sir.. and please don't et too high.. :P and if you are watching a movie please have a companion.... """,
                        'movie_hall':True,'pubs':True,'populated':False}
d['friends']={'explaination':"""Sir... Friends are the choosen ones...Looking at your statement I think that you need a great 
                                companion and quality time with friends.. Let me plan out the best hangout places aound you that you can enjoy with your friends... Call someone.. At this time I cannot provide you with great humor as I myself being in a developpment phase.. :D :D """,'pubs':True,'drinks':True,'smoking':True,'water_park':True,'movie_hall':True}
                                
#print d[sys.argv[1]]['explaination']
fp=open('inntell.json','wb')
json.dump(d,fp)
