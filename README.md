# Store Plugin
This plugin allows server owners to accept payments directly through the game without going through a WebStore such as CraftingStore or BuyCraft.

To download the plugin, visit: https://github.com/Cowings/Store/releases
Note: You must have a PayPal buisness account to use this plugin. Register a buisness account here (completely free): https://www.paypal.com/bizsignup

Features:
* **Completely configurable and dynamic** - all GUIs and Menus are fully configurable. This includes titles, items, rows, everything!
* **Easy to use** - for both users and admins, it's easy to use with minimal setup. Just put in your API key and setup your items.
* **Free** - available for download and usage at no cost, and permissively licensed so it can remain free forever.

## Frequently Asked Questions
* **What are the commands?** - use /store help to find all commands and usages. (admin commands only show up if you have perms)
* **I have a PayPal buisness account, how do I get the secret and client-id?** - create an APP here: https://developer.paypal.com/developer/applications
* **What are the permission nodes?** store.admin to use "/store reload" & "/lookup"
* **I found a bug, what do I do?** - open a new issue at https://github.com/Cowings/Store/issues

## Building
Store Plugin uses Maven to handle dependencies & building.

#### Requirements
* Java 8 JDK or newer
* Git

#### Compiling from source
```sh
git clone https://github.com/Cowings/Store
git clone https://github.com/Derkades/Derkutils/tree/legacy
cd Derkutils
mvn clean install
cd
cd Store/
mvn clean install
```

## Contributing
#### Pull Requests
If you make any changes or improvements to the plugin which you think would be beneficial to others, please consider making a pull request to merge your changes back into the upstream project. (especially if your changes are bug fixes!)

Store Plugin loosely follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Generally, try to copy the style of code found in the class you're editing. 

Please make sure to have bug fixes/improvements in seperate pull requests from new features/changing how features work.

#### TODO
* More spigot jar support
* More testing
* Optimizations

## License
Store Plugin is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/Cowings/Store/blob/master/LICENSE.txt) for more info.
