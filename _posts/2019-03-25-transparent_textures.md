---
layout: post
author: zen
title: Transparent Textures Support
---
### Transparent textures are finally supported!

The main problem currently with Cubyz was that leaves were opaque, that isn't logical and i wanted something better..

Jungle Engine arleady supported them via GL_BLEND and other things, however, i looked in code and found out that
it was the TextureConverter fault, which simply ONLY outputted RGB (no transparency) images!

As a temporary fix, i set a flag in the oak leaves to say the texture is "converted" (it doesn't go through TextureConverter):

![Image1](https://cdn.discordapp.com/attachments/475297969609113600/559709892907106322/unknown.png)

Ok good, transparency works, however i needed to make it standard, so i added support for transparency in the TextureConverter,
and i removed the "converted" flag of the block, however, first try, it was still solid, i thought a bit.. and found out it was due
to the cache not being deleted, i deleted it and it worked as seen below:

![Image2](https://cdn.discordapp.com/attachments/475297969609113600/559710948458233856/unknown.png)

However, to avoid me having to think about the texture cache not being deleted, i made a mechanism that automatically deletes
each cache entry **AT START**.
