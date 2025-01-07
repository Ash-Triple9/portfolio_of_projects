# Analysis of Crime and Income in Tucson: A Machine Learning Project

**Description**

This is a two-person machine learning project that aimed at first analysing a trend between certain factors when it came to crimes occurring in Tucson, Arizona. After the factors were identified (income and type of crime committed), two machine learning models were created based on two hypotheses to determine whether we could predict future crimes (and of what type) in the neighborhoods of Tucson.

**Technologies used**

We used Python programming language in Google Colab to implement our ideas and observe our findings. Our primary machine learning library was scikit-learn, along with other visualization and software libraries (see .ipynb file for a full list of imports)

**Summary of project**

We initially began with obtaining databases from tucsonaz.gov pertaining to crimes reported, neighborhood information etc. After skimming through the databases, we hypothesized that there might be a relationship between the income source of a particular neighborhood and the crime frequency in that area. We also hypothesized whether the type of crime commited (assault, theft etc.) had anything to do with the neighborhood it was committed in. 

Exploratory data analysis of our desired databases (crime and neighborhood) confirmed our hypotheses and after cleaning up the data (removing empty rows, anomalous values etc.) we implemented our two learning models the hypotheses. 

The first learning model was a multinomial regression model that aimed to predict the arrests per household of a neighborhood given the income. Although not giving us a good R^2 value (an arbritrary scalar between -1 and 1 to indicate how well a model behaves with new data), it was a good learning model to showcase our findings.

The second learning model was a random tree classifier to predict the prevalent crime type in a neighborhood. Again, even though the confidence detectors (precision, recall etc.) weren't ideal, it just meant that there was not a solid enough trend between the type of crimes and the neighborhood it occurred in.

To see a detailed documentation of our findings, you can go to the following google doc: https://docs.google.com/document/d/1ldmHuLQZa5c0GG7wCojoj6zXrAsQZNnnyW2vqSe6-H0/edit?usp=sharing 
