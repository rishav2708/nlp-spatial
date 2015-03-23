# nlp-spatial
Gives a local spatial search based on people you are with and what is your mood.
The project is based on the basis of cosine similarity between term vectors and query vectors 
that has been achieved using the lucene indexing.
The approach that I have followed here is a kind of supervised learning.
I had predefined class based on different kinds of moods.
Once the query matches the class the code redirects to a toally different aspect.
The other aspect is where search is performed on a nearby area using the neo4j-spatial extension.
Here the class is taken and restaurants and places that match the type of class are displayed.
The whole project is a basis of a recommendation system and is incorporated in my website.
Later I will try to incorporate it with statistical tools to provide more anlalysis.
One of the methods we will be using is K nearest neighbors.

