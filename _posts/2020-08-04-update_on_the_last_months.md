---
layout: post
author: quantum
title: Update on the last Months
---
In the last months a lot has happened. Especially in April/May 2020. This gets especially clear if you look at the commit history, where you can see a huge spike in april:

![image](/assets/images/blog/github_contributions_2020-08-04.png)

In this blog post I try to give a summary of all those changes:

## Random Ores
This is probably the biggest change in Cubyz. I added ores that get procedurally generated based on the world seed.

The intention behind adding procedurally generated content is that the player will always be able to encounter something uniquely new every time they play.
And because the system is seed-based, people can share their content with each other by just sharing the seed.

For now the ores can only be used to create tools, but that will change, soon.

The names are generated from existing material names. Here are some example names generated with the current system: Rutil, Bystadium, Nisite, Jerfornet, Iodor

For the texture I developed a simple system that is able to draw it's own pixel art:

![image](/assets/images/blog/random_ore4×4.png)

## World Generation
I reworked the biome generation multiple times which eventually lead to the kind of terrain that you can see in the background right now.

I also added round trees, rivers, more biomes and a special cave type: Crystal caverns:

![image](/assets/images/blog/crystal_caverns_2020-04-27.png)

## Lighting
I added a lighting system(similar to the ones in most other voxel games) which, unlike others, supports rgb colors instead of only grayscale.

The system also allows for a finer control of absorption. For example under water the red and green parts of the spectrum get absorbed faster than the blue parts:

![image](/assets/images/blog/underwater_lighting_2020-05-04.png)

## Toruses
We decided to make Cubyz a space exploration game with many planets to discover which each have new random ores and more.

Since the way our worlds(and in general most worlds you have encountered so far) wrap around at the borders is topolically a torus and not a sphere, we decided to call the planets toruses and display them as such.

There is still a lot of work to be done here, but the general concept exists.

## Entities
We added entities into the game. This includes blockdrops(as entities) and pigs.

## Addons
The next release includes a parser that parses simple, human readable data files, that don't need any programming experience to create and allow everyone to add new stuff to the game.

## Others
- Overall performance increase through various changes
- Support for rotated blocks
- A lot of bug fixes, most notably the water surface bug
- Settings
- World creation and loading GUI
- More Blocks
- Better Graphics
- Item Tooltips
- Code refactorings making the whole project more readable and maintainable.
