Instituto Superior Técnico, Universidade de Lisboa

**Network and Computer Security**

# Lab guide: Java Cryptographic Mechanisms

## Goals

- Use the cryptographic mechanisms available in the Java platform.
- Perform attacks exploiting vulnerabilities introduced by the bad use of cryptography.

## Introduction

This laboratory assignment uses Java Development Kit (JDK) version 7 or later, running on Linux. The Java platform strongly emphasizes security, including language safety, cryptography, public key infrastructure, secure communication, authentication and access control.

The Java Cryptography Architecture (JCA), which is a major piece of the Java platform, includes a large set of application programming interfaces (APIs), tools, and implementations of commonly-used security algorithms, mechanisms, and protocols. It provides a comprehensive security framework for writing applications and also provides a set of tools to securely manage applications.

The JCA APIs include abstractions for secure random number generation, key generation and management, certificates and certificate validation, encryption (symmetric/asymmetric block/stream ciphers), message digests (hashes), and digital signatures. Some examples are the MessageDigest, Signature, KeyFactory, KeyPairGenerator, and Cipher classes.

Implementation independence, in the Java platform, is achieved using a _provider_-based architecture. The term Cryptographic Service Provider (CSP) refers to a package or set of packages that implement one or more cryptographic services, such as digital signature algorithms, message digest algorithms, and key conversion services. A program may simply request an object, e.g., a MessageDigest object, implementing a particular service, e.g., the SHA-256 digest algorithm, and get an implementation from one of the installed providers. A program may instead request, if necessary, an implementation from a specific provider.

To obtain a security service from an underlying provider, applications rely on the relevant getInstance() method. The message digest creation, for example, represents one type of service available from providers. To obtain an implementation of a specific message digest algorithm, an application invokes the getInstance() method in the java.security.MessageDigest class.

```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
```

Optionally, by indicating the provider name, the program may request an implementation from a specific provider as in the following:

```java
MessageDigest md = MessageDigest.getInstance("SHA-256";, "MyProvider");
```

Providers may be updated transparently to the application when faster or more secure versions are available. In the Java platform, the java.security.Provider class is the base class for all security providers. Each CSP contains an instance of this class which contains the provider&#39;s name and lists all the security services/algorithms it implements. Multiple providers may be configured at the same time and are listed in order of preference. The highest priority provider that implements that service is selected when a security service is requested.

For more information, please read:

[http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#Introduction](http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html#Introduction)


## Cryptographic mechanisms

Copy the lab files into /tmp/JavaCrypto and change your working directory to it 

```bash
$ cd /tmp/JavaCrypto 
```

Compile the code:

```bash
$ javac src/pt/ulisboa/tecnico/meic/sirs/
```
You will also need to define the Classpath environment variable. 
The Classpath is a parameter that specifies the location of user-defined classes and packages. 
(for .class files in a named package, the Classpath must end with the directory that contains the first package in the full package name).

```bash
$ export CLASSPATH="/tmp/JavaCrypto/src"
```

Please notice that all steps that follow expect that this was done, so you must change commands to an alternative location if used.

**Note** : For every java command, please write the full package names and file paths. They are ommitted for brevity in this guide.

```bash
$ java pt.ulisboa.tecnico.meic.sirs.RandomImageGenerator # instead of just $ java RandomImageGenerator
```

In the directory intro/inputs, you can find 3 different images:

- Tecnico: \*.png, the IST logo
- Tux: \*.png, Tux, the Linux penguin
- Glider: \*.png, the hacker emblem ([http://www.catb.org/hacker-emblem/](http://www.catb.org/hacker-emblem/))

Each one is presented with three different dimensions: 480x480, 960x960, and 2400x2400. 
The resolution number is part of the file name. 
The ImageMixer class is available to facilitate the operations on images. 
Different code examples are available, such as the RandomImageGenerator, ImageXor, and ImageAESCipher classes.

### One-Time Pads (Symmetric stream cipher)

When they could be correctly used, one-time pads would provide perfect security. 
One of the constraints to make them work as expected is that the key stream must never be reused. 
The following steps visually illustrate what happens if they are reused, even if just once:

- Generate a new 480x480 random image

```bash
$ java RandomImageGenerator intro/outputs/otp.png 480 480
```

- Perform the bitwise eXclusive OR operation (XOR) with the generated key

```bash
$ java ImageXor intro/inputs/tecnico-0480.png intro/outputs/otp.png intro/outputs/encrypted-tecnico.png
```

TO BE CONTINUED
