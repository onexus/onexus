package org.onexus.collection.store.elasticsearch;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.IEntitySet;
import org.onexus.collection.loader.tsv.internal.TsvLoader;
import org.onexus.collection.store.elasticsearch.mocks.MockDataManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Progress;
import org.onexus.resource.serializer.xstream.internal.ResourceSerializer;

public class AbstractCollectionTest {

    private Class projectRoot;
    private String projectUrl;

    private ResourceSerializer serializer;
    private TsvLoader loader;

    public AbstractCollectionTest(Class projectRoot, String projectUrl) {
        this.projectUrl = projectUrl;
        this.projectRoot = projectRoot;

        // Resource serializer
        serializer = new ResourceSerializer();
        serializer.register(Collection.class);

        // Collection loader
        loader = new TsvLoader();
        loader.setDataManager(new MockDataManager(projectRoot));

    }

    protected Collection getCollection(String path) {
        return serializer.unserialize(
                Collection.class,
                new ORI(projectUrl, path.replace(".onx", "")),
                projectRoot.getResourceAsStream(path)
        );
    }

    protected IEntitySet readCollection(Collection collection) {

        Progress progress = new Progress("test", "test");
        Plugin plugin = new Plugin("tsv-loader", "");
        try {
            return loader.newCallable(progress, plugin, collection).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
