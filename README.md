# ReadKeyWord
Keyword Extractor for [ReadKeyRSS](https://github.com/EDFward/ReadKeyRSS) reader, 
interacting with [ReadKeyServer](https://github.com/EDFward/ReadKeyServer).

******

*ReadKeyWord* is a server application extracting a series of keywords from given texts. 
The keyword server communicates with feed handler in *ReadKeyServer* using HTTP POST requests 
where feed handler prepares the textual content, number of keywords and corresponding language 
(currently only supports Chinese and English for keyword extraction).

Keyword server is developed using Java with [Spark web framework](https://github.com/perwendel/spark) 
(not Apache Spark, the large-scale data processing engine). For Chinese texts, it uses [HanLP](https://github.com/hankcs/HanLP), 
a Chinese NLP open-source library which provides off-the-shelf module for keyword extraction based on 
[TextRank algorithm](https://web.eecs.umich.edu/~mihalcea/papers/mihalcea.emnlp04.pdf); 
for English, I’ve chosen a Python implementation of [RAKE (Rapid Automatic Keyword Extraction) 
algorithm](http://www.researchgate.net/profile/Stuart_Rose/publication/227988510_Automatic_Keyword_Extraction_from_Individual_Documents/links/55071c570cf27e990e04c8bb.pdf) 
([github link](https://github.com/aneesha/RAKE)) and integrated it as a Jython application which 
gives interoperability in the Java server.

