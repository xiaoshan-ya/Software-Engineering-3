package uk.ac.wlv.sentistrength.wordsresource;

import uk.ac.wlv.sentistrength.classificationresource.ClassificationOptions;

public interface WordResource {

    public boolean initialise(String sFilename, ClassificationOptions options);

//    public boolean initialise(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude);
}
