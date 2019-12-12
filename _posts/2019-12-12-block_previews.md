---
layout: post
author: zen
title: Block Previews
---

### 3D Block Icons

For now Cubyz had to make separate textures for block items as there weren't any system to dynamically show 3D blocks. So i decided to add it. The first concern was: How?  
Making it part of the rendering would be resource-intensive and complex to setup. So this wasn't possible, but i thought of another solution: Using a separate render that draws to a framebuffer.

#### What is a framebuffer?
A framebuffer is a way to capture a render onto a color texture. It internally also captures depth testing but it is unreadable from the CPU. The texture is generated before capture when `genColorTexture(width, height)` is called and the framebuffer data is copied onto it. The framebuffer itself is a combination of the said color texture and a render buffer. The renderbuffer is where the capture actually happens and the color texture is where the image is saved to.

#### The actual rendering
The rendering is made by creating a Chunk with no world attached to it. The chunk will contain an instance of the block at coordinates 0,0,0.
The chunk then reveals this same instance. Now the local player (so the camera) position and rotation are changed to some values to be able to see the block. Then the Framebuffer is instanced, the color texture and render buffer are generated, and it is binded. The clear color is set to full transparent color and the viewport set to the size of the image (128x128). The HUD is disabled and orthogonal projection enabled. And then the render occurs in `MainRenderer` with a full brightness ambient light, meaning it will be rendered like a world.

#### Cleaning up
̀If we exited just after rendering, the viewport, projection, clear color and many would be entirely messed up. In fact due to the framebuffer being binded, the render would not even appear to window. So the rendering is cleaned, it's basically a step back for every initialization operation done before rendering, so disabling orthogonal projection and enabling HUD, the viewport is set back to window width and height and the player position and rotation got back to what they were before rendering. The clear color will be back to normal at next rendering (as it is always set).

#### Back To The Screen I
Now to render the resulting texture to the screen, the texture ID must simply be passed to a bind that NanoVG provides to transform OpenGL texture IDs to NanoVG texture IDs, this NanoVG texture ID is then saved and will be re-used in later renders. And now the normal item in inventory renders happen.
