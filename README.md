Instituto Superior Técnico, Universidade de Lisboa

**Network and Computer Security**

# Lab guide: Java Cryptographic Details

Cryptography is more complex than just using certain functions; it is a field where details matter greatly.
Selecting the right algorithm and key size, and correctly applying encryption modes, are crucial for real security.
Small mistakes in these areas can lead to vulnerabilities.

This lab guide emphasizes understanding security vulnerabilities and best practices in cryptographic implementations.
It starts with a quick review of the Java Cryptography Architecture (JCA), covering essential cryptographic concepts like secure random number generation, key management, and encryption techniques.
Through exercises, it explores symmetric and asymmetric encryption, block cipher modes (including ECB, CBC, and OFB), digital signatures with OpenSSL, and a file tampering exercise.

The goals are:

- Correctly use the cryptographic mechanisms available in the Java platform;
- Perform attacks that exploit vulnerabilities introduced by the bad use of cryptography.

## Cryptography in the Java Platform

The Java platform strongly emphasizes security, including language safety, cryptography, public key infrastructure, secure communication, authentication, and access control.  
The Java Cryptography Architecture (JCA), which is a major piece of the Java platform, includes a large set of application programming interfaces (APIs), tools, and implementations of commonly-used security algorithms, mechanisms, and protocols.
It provides a comprehensive security framework for writing applications and also provides a set of tools to securely manage applications.

The JCA APIs include abstractions for secure random number generation, key generation and management, certificates and certificate validation, encryption (symmetric/asymmetric block/stream ciphers), message digests (hashes), and digital signatures.
Some examples are the `MessageDigest`, `Signature`, `KeyFactory`, `KeyPairGenerator`, and `Cipher` classes.

Implementation independence, in the Java platform, is achieved using a _provider_-based architecture.
The term Cryptographic Service Provider (CSP) refers to a package or set of packages that implement one or more cryptographic services, such as digital signature algorithms, message digest algorithms, and key conversion services.
A program may simply request an object, e.g., a `MessageDigest` object, implementing a particular service, e.g., the SHA-256 digest algorithm, and get an implementation from one of the installed providers.
A program may instead request, if necessary, an implementation from a specific provider.

To obtain a security service from an underlying provider, applications rely on the relevant `getInstance()` method.
The message digest creation, for example, represents one type of service available from providers.
To obtain an implementation of a specific message digest algorithm, an application invokes the `getInstance()` method in the [`java.security.MessageDigest` class](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/MessageDigest.html).

```java
MessageDigest md = MessageDigest.getInstance("SHA-256");
```

Optionally, by indicating the provider name, the program may request an implementation from a specific provider as in the following:

```java
MessageDigest md = MessageDigest.getInstance("SHA-256", "MyProvider");
```

Providers may be updated transparently to the application when faster or more secure versions are available.
In the Java platform, the `java.security.Provider` class is the base class for all security providers.
Each CSP contains an instance of this class which contains the provider's name and lists all the security services/algorithms it implements.
Multiple providers may be configured at the same time and are listed in order of preference.
The highest priority provider that implements that service is selected when a security service is requested.

For more information, please read the [Java Cryptography Architecture (JCA) Reference Guide](https://docs.oracle.com/en/java/javase/11/security/java-cryptography-architecture-jca-reference-guide.html#GUID-2BCFDD85-D533-4E6C-8CE9-29990DEB0190).

## Setup

This laboratory assignment uses Java Development Kit (JDK) version 11 or later, running on Linux.  
It is recommended that you follow this assignment using the virtual machine from previous classes.

First, check if you have the Java compiler and Maven available.  
If not, install Maven and the Java compiler:

```bash
sudo apt update
sudo apt install maven
sudo apt install openjdk-11-jdk
```

To try the cryptographic mechanisms, the Java code needs to be compiled and executed.

Put the lab files in a working folder with write permissions, like `/tmp/crypto`, for example, and change your working directory to it:

```bash
cd /tmp/crypto 
```

You can compile the code using [Maven](https://maven.apache.org/).
To do so:

```bash
mvn clean compile
```

<!--
To execute a class with arguments using Maven, write something like:

```bash
mvn exec:java -Dmainclass=pt.ulisboa.tecnico.meic.sirs.RandomImageGenerator -Dexec.args="intro/outputs/otp.png 480 480"
```

You can also modify the class and arguments directly in the `pom.xml` file.
-->

To compile everything and generate convenient scripts for running each class:

```bash
mvn install
```

The commands include:

- `aes-key-gen`, a utility for generating AES keys;
- `base64-decode`, decodes Base64 encoded data;
- `base64-encode`, for encoding data into Base64 format;
- `file-aes-cipher`, applies AES Cipher to files;
- `file-aes-decipher`, used for deciphering files encrypted with AES;
- `image-aes-cipher`, encrypts images using AES Cipher;
- `image-xor`, applies the XOR operation on images;
- `random-image-gen`, a tool for creating random images; and
- `rsa-key-gen`, which generates RSA keys.

These commands are generated in `target/appassembler/bin`.  
Let us add this folder to the current `PATH` environment variable, so we can call the commands in any directory.

The following commands find the absolute path of the work directory, export the variable for the current session, and test one of the commands:

```bash
pwd

export PATH=$PATH:/tmp/crypto/target/appassembler/bin

image-xor
```

Adjust `/tmp/crypto` to the directory you are actually using.  
The `image-xor` command should be recognized and print information about the missing parameters.

The following commands are the Windows CMD equivalent:

```cmd
cd

set PATH=%PATH%;C:\tmp\crypto\target\appassembler\bin

image-xor
```

<!--
```powersh
Get-Location

$env:PATH += ";C:\tmp\crypto\target\appassembler\bin"

image-xor
```
-->

### Image files

The cryptographic operations will be applied to image files, so that its results can be "seen".
In the directory `intro/inputs`, you can find three different images:

- Técnico: \*.png, the IST logo;
- Tux: \*.png, Tux, the Linux penguin;
- Glider: \*.png, the hacker emblem ([http://www.catb.org/hacker-emblem/](http://www.catb.org/hacker-emblem/)).

Each one is presented with three different dimensions: `480x480`, `960x960`, and `2400x2400`.
The resolution number is part of the file name.
The `ImageMixer` class is available to facilitate the operations on images.
Different code examples are available, such as the `RandomImageGenerator`, `ImageXor`, and `ImageAESCipher` classes.

## One-Time Pads (Symmetric stream cipher)

If they could be correctly used in practice, one-time pads would provide perfect security.
One of the constraints to make them work as expected is that the key stream must never be reused.
The following steps visually illustrate what happens if they are reused, even if just once:

Generate a new `480x480` random image:

```bash
random-image-gen intro/outputs/otp.png 480 480
```

Perform the bitwise eXclusive OR operation (XOR) with the generated key:

```bash
image-xor intro/inputs/tecnico-0480.png intro/outputs/otp.png intro/outputs/encrypted-tecnico.png
```

XOR tux-0480.png with the same generated key:

```bash
image-xor intro/inputs/tux-0480.png intro/outputs/otp.png intro/outputs/encrypted-tux.png
```

Watch the images `encrypted-tecnico.png` and `encrypted-tux.png`.  
Switch between them and see the differences.  
To make the differences obvious, XOR them together:

```bash
image-xor intro/outputs/encrypted-tecnico.png intro/outputs/encrypted-tux.png intro/outputs/tecnico-tux.png
```

You can see that the reuse of a one-time pad (or any stream cipher key at all) considerably weakens (or completely breaks) the security of the information.
The reason is the following:

```txt
C1 = M1 ⊕ K

C2 = M2 ⊕ K

C1 ⊕ C2 = M1 ⊕ M2
```

Legend: C stands for cipher-text, M for plain-text, K for key, ⊕ for XOR

The result you get is the XOR of the images.
You can experiment with other images and sizes.

## Block Cipher Modes

Now that you know that keys should never be reused, remember that the way you use them is also important.

We will use a symmetric-key encryption algorithm working in blocks to encrypt the pixels from an image.
We will use different modes, namely:
ECB (Electronic Code Book), CBC (Cipher Block Chaining) and OFB (Output FeedBack).

### ECB (Electronic Code Book)

In the ECB mode, each block is independently encrypted with the key:

```txt
C[i] = E_k(M[i])
```

[Visual representation of ECB mode](ECB.png)

Begin by generating a new AES Key:

```bash
aes-key-gen w intro/outputs/aes.key
```

Then, encrypt the glider image with it:

```bash
image-aes-cipher intro/inputs/glider-0480.png intro/outputs/aes.key ECB intro/outputs/glider-aes-ecb.png
```

Watch the output image.  
Remember what you have just done: encrypted the image with AES, using ECB mode, and a key you generated yourself.

Try the same thing with the other images (especially with other sizes).

**NOTE**: The observable visual encryption pattern emitted by ECB mode can be observered independently of the chosen security provider and the generated AES key as it is a consequence of its operational design.

### CBC (Cipher Block Chaining)

In CBC mode, each block M[i] is XORed with the ciphertext from the previous block, and then encrypted with key k:

```txt
C[i] = E_k (M[i] ⊕ C[i-1])
```

[Visual representation of CBC mode](CBC.png)

The encryption of the first block can be performed by means of a random and unique value known as the _Initialization Vector_ (IV).

The AES key will be the same from the previous step.

Encrypt the glider image with it, this time replacing ECB with CBC:

```bash
image-aes-cipher intro/inputs/glider-0480.png intro/outputs/aes.key CBC intro/outputs/glider-aes-cbc.png
```

Watch the file `glider-aes-cbc.png`.  
See the difference made by changing only the mode of operation.

Still in the CBC mode, you may have wondered why the IV is needed in the first block.
Consider what happens when you encrypt two different images with similar beginnings, and with the same key: the initial cipher text blocks would also be similar!

The `ImageAESCipher` class provided has been deliberately weakened: instead of randomizing the IV, it is always the same.

This time, encrypt the other two images with AES/CBC, still using the same AES key:

```bash
image-aes-cipher intro/inputs/tux-0480.png intro/outputs/aes.key CBC intro/outputs/tux-aes-cbc.png

image-aes-cipher intro/inputs/tecnico-0480.png intro/outputs/aes.key CBC intro/outputs/tecnico-aes-cbc.png
```

Now watch the images `glider-aes-cbc.png`, `tux-aes-cbc.png`, and `tecnico-aes-cbc.png`.
Look to the first lines of pixels.
Can you see what is going on?

### OFB (Output FeedBack)

In the OFB mode, the IV is encrypted with the key to make a keystream that is then XORed with the plaintext to make the cipher text.

[Visual representation of OFB mode](OFB.png)

In practice, the keystream of the OFB mode can be seen as the one-time pad that is used to encrypt a message.
This implies that in OFB mode, if the key and the IV are both reused, there is no security.

Encrypt the images with OFB:

```bash
image-aes-cipher intro/inputs/glider-0480.png intro/outputs/aes.key OFB intro/outputs/glider-aes-ofb.png

image-aes-cipher intro/inputs/tux-0480.png intro/outputs/aes.key OFB intro/outputs/tux-aes-ofb.png

image-aes-cipher intro/inputs/tecnico-0480.png intro/outputs/aes.key OFB intro/outputs/tecnico-aes-ofb.png
```

Remember that the `ImageAESCipher` implementation has been weakened, by having a null IV, and you are reusing the same AES key.  
Watch the generated images and switch quickly between them.

Take two images (e.g., image1 and image2) and cipher them both.
XOR image1 with the ciphered image2.  
What did you obtain?
Why?

What is more secure to use: CBC or OFB?

## Asymmetric ciphers

The goal now is to use asymmetric ciphers, with separate private and public keys.
RSA is the most well known of these algorithms.

### Generating a pair of keys with OpenSSL

Generate the key pair for the server:

```bash
openssl genrsa -out server.key
```

And for the user:

```bash
openssl genrsa -out user.key
```

### Generating a self-signed certificate

Create a Certificate Signing Request for the server, using its key (you can leave the parameters empty):

```bash
openssl req -new -key server.key -out server.csr
```

Create a configuration file for the certificate:

```bash
echo "[v3_ca]\nbasicConstraints = CA:TRUE" > server_cert_config.cnf
```

Self-sign:

```bash
openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt -extfile server_cert_config.cnf -extensions v3_ca
```
**NOTE**: Alternatively, since our configuration is very simple, we could immediately generate the certificate, skipping the creation of the CSR and the configuration file:

```bash
openssl req -x509 -days 365 -key server.key -out server.crt -addext "basicConstraints=CA:TRUE"
```

### Signing a certificate

Generate a Certificate Signing Request for the user, using its key:

```bash
openssl req -new -key user.key -out user.csr
```

And sign it with the server's private key:

```bash
openssl x509 -req -days 365 -in user.csr -CA server.crt -CAkey server.key -out user.crt
```

Verify the user's certificate:
```bash
openssl verify -CAfile server.crt user.crt
```
user.crt: OK

### Signing a file

Create a digest of the `grades.txt` file:
```bash
openssl dgst -sha256 -binary grades/inputs/grades.txt > grades/outputs/grades.sha256
```

Sign the digest with the user's private key:

```bash
openssl pkeyutl -sign -inkey user.key -in grades/outputs/grades.sha256 -out grades/outputs/grades.sig
```

Verify the signature with the user's public key:

```bash
openssl pkeyutl -verify -inkey user.key -sigfile grades/outputs/grades.sig -in grades/outputs/grades.sha256
```
Signature Verified Successfully

Try modifying the `grades.sha256` file and rerunning the verification to see what happens.
### Reading the generated pair of keys with Java

To read the generated keys in Java it is necessary to convert them to the right format.

Convert `server.key` to the `.pem` format:

```bash
openssl rsa -in server.key -text > private_key.pem
```

Convert private Key to PKCS#8 format (so the Java library can read it):

```bash
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
```

Output public key portion in `.der` format (so Java can read it):

```bash
openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
```

Read the key files using the following command:

```bash
rsa-key-gen r private_key.der public_key.der
```

### Generating a pair of keys with Java

Generate a new pair of RSA Keys.

```bash
rsa-key-gen w intro/outputs/priv.key intro/outputs/pub.key
```

Based on the `ImageAESCipher` class create `ImageRSACipher` and `ImageRSADecipher` classes.

Encrypt the image with the public key and then decrypt it with the private key.
Try the same thing with the other images - especially with other sizes.

Please consider that the RSA cipher, as implemented by Java, can only be applied to one block of data at a time, and its size depends on the size of the key.
For a 1024-bit key, the block size is 117 bytes or 60 bytes, depending on the padding options.
This is acceptable because the RSA cipher is mostly used to cipher keys or hashes that are small.
For large data, hybrid cipher is most suited, combining RSA with AES, for example.
For this exercise you can cipher one block at a time.

## File tampering exercise

In the directory `grades/inputs`, you can find the file `grades.txt`, the plaintext of a file with the grades of a course.
This flat-file database has a rigid structure: 64 bytes for name, and 16 bytes for each of the other fields, number, age and grade.
Unfortunately, you happen to be _Mr. Thomas S. Cook_, and your grade was not on par with the rest of your class because you studied for a different exam...

Begin by encrypting this file into the `ecb.aes` file. 
For this example, we will still reuse the AES key generated above and ECB mode.

```bash
file-aes-cipher grades/inputs/grades.txt intro/outputs/aes.key ECB grades/outputs/grades.ecb.aes
```

Keeping in mind how the mode operations work, and without using the secret key, try to change your grade to 21 in the encrypted files or give everyone in class a 20.  
(Why 21 or all 20s? Because you are an _ethical hacker_ using your skills to show that the system is vulnerable, not perform actual cheating.)
* For this exercise, it may be useful to inspect the file with a hex editor, such as `hexedit` (GUI), `hexdump` or `xxd`.

Did you succeed?
Did your changes have side effects?

Now try to attack `cbc.aes` and `ofb.aes`.
For this example, we will still reuse the AES key generated above but use the CBC and OFB modes.

```bash
file-aes-cipher grades/inputs/grades.txt intro/outputs/aes.key CBC grades/outputs/grades.cbc.aes

file-aes-cipher grades/inputs/grades.txt intro/outputs/aes.key OFB grades/outputs/grades.ofb.aes
```

How do you compare the results with ECB?

Since the inputs and outputs of cryptographic mechanisms are byte arrays, in many occasions it is necessary to represent encrypted data in text files.  
A possibility is to use [Base 64 Encoding](https://en.wikipedia.org/wiki/Base64) that, for every binary sequence of 6 bits, assigns a predefined ASCII character.
Execute the following to create a Base 64 representation of files previously generated.

```bash
base64-encode grades/outputs/grades.cbc.aes grades/outputs/grades.cbc.aes.b64
```

Decode them:

```bash
base64-decode grades/outputs/grades.cbc.aes.b64 grades/outputs/grades.cbc.aes.b64.decoded
```

Check if they are similar using the `cmp` command (or `fc /b` command on Windows):

```bash
cmp grades/outputs/grades.cbc.aes grades/outputs/grades.cbc.aes.b64.decoded
```

It should not return anything.

Check the difference on the file sizes between ```grades/outputs/grades.cbc.aes``` and ```grades/outputs/grades.cbc.aes.b64```.  
Can you explain it?
In percentage, how much is it?

Does Base 64 provide any kind of security?  
If so, how?

Use Java to generate the message authentication code (MAC) and digital signature of the grades file.
By performing these operations, which security requirements can be guaranteed?  

## Conclusion

This lab guide provided further exploration of cryptographic and data processing tools in Java, including:
one-time pads (symmetric stream cipher), block cipher modes (including ECB, CBC, and OFB), and asymmetric ciphers (focusing on RSA).  
Additionally, it included a practical exercise on file tampering, demonstrating how encryption alone may not guarantee data integrity.

----

[SIRS Faculty](mailto:meic-sirs@disciplinas.tecnico.ulisboa.pt)
