# SolidTX - Network Data Object Stack

[![Build Status](https://travis-ci.org/binarybabel/solid-tx.svg?branch=master)](https://travis-ci.org/binarybabel/solid-tx)
[![Join the chat at https://gitter.im/solid-tx/Lobby](https://badges.gitter.im/solid-tx/Lobby.svg)](https://gitter.im/solid-tx/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Developed by BinaryBabel for the MinePass Network, and released open source for transparency and community benefit.

SolidTX provides asynchronous bulk transfer of remote server data to/from Java objects,
while requiring no external dependencies.

It is designed with the ambition that plugins and/or API developers need reliable ways to transport data quickly,
while adapting to network conditions. Multi-thread ready, data operations happen separate to the application's main
execution thread, with deferred callback/closure support.

SolidTX is embeddable to a project's namespace to avoid any external dependencies, prevent version collisions,
and simplify plugin deployment.

## Project Status and Development

SolidTX is under development with respect to platforms where it is in production.

It should be considered **ALPHA** quality for 3rd-party use at this time due to ongoing development.

## Operation

SolidTX transfers data by means of payloads. Each payload request submits the following information:

- Which missing objects are needed
- Existing objects needing an update/operation
- Cached objects that *may* need refreshed

The remote server responds with a payload containing the new/updated objects.

**The server-side source code for managing payload requests is not available at this time.**

## Architecture

SolidTX is designed to be sub-classed by the API designer to create a "Stack" specific to their API and Objects.

**The stack's components are as follows:**

Component | Description | Example(s)
--------- | ----------- | ----------
**NetworkManager** | Manages send/receiving object data. **Sub-Components:**
+ Gateway   | Network transport of byte data | HttpGateway
+ NetworkAdapter | Converts payload requests/responses to byte data | JsonAdapter
**ObjectManager** | Manages data/object conversions
+ DataMapper | Encodes/Decodes different Java object formats     | PojoDataMapper<br>TxDataMapper
**StorageManager** | Stores and caches object data from network. **Sub-Components:**
+ Container | Stores byte data to a storage medium | FileContainer
+ StorageAdapter | Converts object data to byte data | JsonAdapter

## Embedding

The following is a rough example of the embedding process, assuming a `src/main/java` structure.

```
# From your project root...
cd src
medir -p embed/repo embed/java
cd embed

# From the embed directory...
git submodule add https://github.com/binarybabel/solid-tx.git repo/solidtx
cp repo/solidtx/src/embed/* ./    # not recursively
  # this creates 'README.txt' and 'embed-tool.jar'

# Embed into your namespace...
java -jar embed-tool.jar solidtx org.binarybabel.solidtx YOUR_BASE_PACKAGE
```

For future updates simply `git pull` within the submodule and repeat the embed-tool.jar command.

### Sample Gradle Includes

You will need to modify your build to include the embedded code, both for the compile and in the
generated artifact.

```
sourceSets {
    embed
    main {
        compileClasspath += embed.output
        runtimeClasspath += embed.output
    }
    test {
        compileClasspath += embed.output
        runtimeClasspath += embed.output
    }
}

jar {
    from sourceSets.embed.output
    from sourceSets.main.output
}

javadoc {
    classpath = sourceSets.main.compileClasspath
}
```


## Contribution / Collaboration

For Q&A please use https://gitter.im/solid-tx/Lobby

## License

```
The MIT License (MIT)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
