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
MessageDigest md = MessageDigest.getInstance("SHA-256", "MyProvider");
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

- XOR tux-0480.png with the same generated key

```bash
$ java ImageXor intro/inputs/tux-0480.png intro/outputs/otp.png intro/outputs/encrypted-tux.png
```

- Watch the images encrypted-tecnico.png and encrypted-tux.png. 
Switch between them and see the differences.

- Make the differences obvious: XOR them together:

```bash
$ java ImageXor intro/outputs/encrypted-tecnico.png intro/outputs/encrypted-tux.png intro/outputs/tecnico-tux.png
```

You can see that the reuse of a one-time pad (or any stream cipher key at all) considerably weakens (or completely breaks) the security of the information. 
In the following, C stands for cipher-text, M for plain-text and K for key:         

```
C1 = M1 ⊕ K

C2 = M2 ⊕ K

C1 ⊕ C2 = M1 ⊕ M2
```

The result you get is the XOR of the images. 
Experiment with other images and sizes.


### Block cipher modes

Now that you know that keys should never be reused, remember that the way you use them is also important. 
You are about to use a symmetric-key encryption algorithm in modes ECB (Electronic Code Book), CBC (Cipher Block Chaining) and OFB (Output FeedBack), to encrypt the pixels from an image.

#### ECB (Electronic Code Book)

In the ECB mode, each block is encrypted with the key independently:

```
c[i] = E_k (m[i])
```

![ECB](ECB.png)

Begin by generating a new AES Key.

```bash
$ java AESKeyGenerator w intro/outputs/aes.key
```

Then, encrypt the glider image with it:

```bash
$ java ImageAESCipher intro/inputs/glider-0480.png intro/outputs/aes.key ECB intro/outputs/glider-aes-ecb.png
```

Watch the output image. 
Remember what you have just done: encrypted the image with AES, using ECB mode, and a key you generated yourself.

Feel free to try the same thing with the other images (especially with other sizes).

Try using Java providers to generate a new AES key, by creating a DESKeyGenerator based on the AESKeyGenerator class. 
What is necessary to change in the code for that to happen?

Repeat all the previous steps for the new key.

Compare the results obtained using ECB mode with AES with the previous ones. 
What are the differences between them?

#### CBC (Cipher Block Chaining)

In CBC mode, each block m[i] is XORed with the ciphertext from the previous block, and then encrypted with key k: 

```
c[i] = E_k (m[i] ⊕ c[i-1])
```

![CBC](CBC.png)

The encryption of the first block can be performed by means of a random and unique value known as the Initialization Vector (IV). 

The AES key will be the same from the previous step.

Encrypt the glider image with it, this time replacing ECB for CBC:

```bash
$ java ImageAESCipher intro/inputs/glider-0480.png intro/outputs/aes.key CBC intro/outputs/glider-aes-cbc.png
```

Watch the file glider-aes-cbc.png. 
See the difference made by changing only the mode of operation.

Still in the CBC mode, you might have wondered why the IV is needed in the first block. 
Consider what happens when you encrypt two different images with similar beginnings, and with the same key: the initial cipher text blocks will also be similar!

The ImageAESCipher class provided has been deliberately weakened: instead of randomizing the IV, it is always the same.

This time, encrypt the other two images with AES/CBC, still using the same AES key:

```bash
$ java ImageAESCipher intro/inputs/tux-0480.png intro/outputs/aes.key CBC intro/outputs/tux-aes-cbc.png

$ java ImageAESCipher intro/inputs/tecnico-0480.png intro/outputs/aes.key CBC intro/outputs/tecnico-aes-cbc.png
```

Now watch the images glider-aes-cbc.png, tux-aes-cbc.png, and tecnico-aes-cbc.png.
Look to the first lines of pixels. 
Can you see what is going on?


#### OFB

In the OFB mode, the IV is encrypted with the key to make a keystream that is then XORed with the plaintext to make the cipher text.

![OFB](OFB.png)

In practice, the keystream of the OFB mode can be seen as the one-time pad that is used to encrypt a message. 
This implies that in OFB mode, if the key and the IV are both reused, there is no security.

Encrypt the images with OFB:

```bash
$ java ImageAESCipher intro/inputs/glider-0480.png intro/outputs/aes.key OFB intro/outputs/glider-aes-ofb.png

$ java ImageAESCipher intro/inputs/tux-0480.png intro/outputs/aes.key OFB intro/outputs/tux-aes-ofb.png

$ java ImageAESCipher intro/inputs/tecnico-0480.png intro/outputs/aes.key OFB intro/outputs/tecnico-aes-ofb.png
```

Remember that the ImageAESCipher implementation has been weakened, by having a null IV, and you are reusing the same AES key. 
Watch the generated images and switch quickly between them.

Take two images (e.g., image1 and image2) and cipher them both. 
XOR image1 with the ciphered image2. 
What did you obtain? 
Why?

What is more secure to use: CBC or OFB?


TO BE CONTINUED
