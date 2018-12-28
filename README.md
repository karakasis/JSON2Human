# JSON2Human
A Library in Java, that simplifies a JSON file so that a human can read it, with the option to beautify the JSON.

# Methods
 - JSON2Human.toHuman(InputStream json)
 - JSON2Human.beautify(InputStream json)
 - JSON2Human.combined(InputStream json)
 
 # Launch from terminal as a stand-alone
 Arguments
 - -path (where path is your json file)
 - -code (where code : 0 toHuman, 1 beautify, 2 combined)
 
 # Results
 - .beautify : you get a .json on the root of your app
 - .toHuman : you get a .txt and a System.print at the root of your app
