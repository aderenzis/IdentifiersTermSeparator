# IdentifiersTermSeparator
Smart WordNet-based identifier term separator 

# Configuration
>It's necessary to configure a enviroment variable WNHOME="WordNet path".

# Excecution example
> TermSeparator separator= new TermSeparator();

> ArrayList<String> terms = separator.separateTerms("anIdentifier");

> System.out.println(terms);
