# ByteGrep

A Java library/executable used for finding byte sequences in files.  Please note that this project was created as a learning exercise.  It is not thoroughly implemented and is not very fast and so it's not recommended to be used in production.

# Usage

The jar file produced from this project is executable.  Two command line arguments are needed.  The first is the regular expression describing the sequence of bytes to find.  The second argument is a path to the file to be searched:

    java -jar bytegrep.jar some-regex path/to/some/file

If a byte sequence described by the given regular expression is found, the output should look something like:

> Found match at byte offset 72

or

> No match found

if a match was not found.

# Regular Expressions

The regular expression syntax is exactly what you'd expect with one caveat: the literal syntax is different.  Since we are looking for bytes and not characters, the following byte literal syntax is used:

    0xXY

where X and Y are any hexadecimal digits.  So if you wanted to find the following four bytes:

    0xCAFEBABE

you'd use this regular expression syntax:

    0xCA0xFE0xBA0xBE

Grouping, alternation and the quantifiers *, + and ? are supported.  So the following is also valid ByteGrep syntax:

    (0x8F0x45)+0xAA?0x3C

Currently the following meta-characters are not supported:

    []{}^.$

# Issues

Other than the regular expression syntax not yet implemented as mentioned above this implementation does not do any backtracking.  This means that byte sequence described by syntax such as

    0x45*0x450xAA

will not be found because the first part of the expression (0x45*) will consume all the 0x45s before the 0xAA and then the next part of the expression (0x45) will not match.  Backtracking may be implemented in a future version.

# Rationale

As stated previously this project was created as a learning experience.  In particular there are two main concepts I wanted to learn.

## Parsing

The file `DefaultParser.java` is an implementation of what's known as a predictive recursive descent parser.  It's what parses the regular expression syntax string and creates from that a syntax tree representing the regular expression.  There's a nice comment at the top of that file that describes the grammar parsed by the parser in all its gory detail.

## Syntax Trees and the Interpreter and Composite patterns

The class `com.timmciver.bytegrep.RegularExpression` defines the interface (though it's currently an abstract class) that is implemented by the other classes in that package.  All of the other classes (except `RegularExpression` and `LiteralByte`) take one or more `RegularExpression`s in their constructors.  This use of the [Composite Pattern](http://en.wikipedia.org/wiki/Composite_pattern) allows one to build up an arbitrarily complex syntax tree representing a regular expression for a byte sequence.

The match() method defined in RegularExpression and implemented by each of its subclasses is a variation of the [Interpreter Pattern](http://en.wikipedia.org/wiki/Interpreter_pattern).  But instead of executing operations the match() method checks the given input against the regular expression that its subclass represents.
