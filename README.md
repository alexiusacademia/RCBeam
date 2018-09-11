# RCBeam Library

A java library for the analysis of reinforced concrete beams of any section.

### Section Features
- Non-Hollow Sections (for now)
- Any shape (provided with `x,y` coordinates)
- Default unit is millimeter (for now)

### Analysis Results
*(At specific stages.)*

- Moment
- Curvature

### Creating Steel in Tension Object

```java
// Creating object
SteelTension st = new SteelTension();

// Asigning the steel area, 2nd parameter is true if in metric units (in square millimeters)
st.setTotalArea(1200, true);
```

### Creating Steel in Compression Object

```java
// Creating object
SteelCompression sc = new SteelCompression();

// Asigning the steel area, 2nd parameter is true if in metric units (square millimeters)
sc.setTotalArea(1200, true);

// Set the location of compression steel reinf. from extrem concrete compression fiber (in millimeters)
sc.setdPrime(50);
```

### Creating BeamSection Object

```java
// Creating object
BeamSection bs = new BeamSection();

// Defining shape
bs.addNode(new BeamSectionNode(0, 0));
bs.addNode(new BeamSectionNode(0, 500));
bs.addNode(new BeamSectionNode(300, 500));
bs.addNode(new BeamSectionNode(300, 0));

// Define properties
bs.setFcPrime(21);
bs.setEffectiveDepth(450);
bs.setFy(275);

// Define reinforcements
bs.setSteelTension(st);
bs.setSteelCompression(sc);
```

### Creating an Analysis

```java
// Creating an object, specifying the beam section to be analyzed
BeamAnalyses analyses = new BeamAnalyses(bs);

// Defining a result from the analysis
BeamAnalysisResult uncrackAnalysis = analyses.beforeCrackAnalysis();

// Accessing the result
// Cracking moment
System.out.println(uncrackAnalysis.getMomentC());
// Curvature right before cracking
System.out.println(uncrackAnalysis.getCurvatureC());
// Minimum tensile reinforcement as required by ACI code
System.out.println(uncrackAnalysis.getMinimumSteelTensionArea());
```



### How to Contribute

Anyone is welcome to contribute via coding or anything. For coding contributions, please refer to the issues and find the topic that best suits you.
If for a reason the topic you want to contribute isn't listed yet, feel free to create the issue and I'll try to respond as quickly as I could.