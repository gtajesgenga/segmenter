module.exports = function follow(api, rootPath, relArray, embedded) {
    const root = api({
        method: 'GET',
        path: rootPath
    });

    return relArray.reduce(function(root, arrayItem) {
        const rel = typeof arrayItem === 'string' ? arrayItem : arrayItem.rel;
        return traverseNext(root, rel, arrayItem, embedded);
    }, root);

    function traverseNext (root, rel, arrayItem, embedded) {
        return root.then(function (response) {
            if (hasEmbeddedRel(response.entity, rel)) {
                return embedded ? response.entity._embedded[rel] : response;
            }

            if(!response.entity._links) {
                return [];
            }

            if (typeof arrayItem === 'string') {
                return api({
                    method: 'GET',
                    path: response.entity._links[rel].href
                });
            } else {
                return api({
                    method: 'GET',
                    path: response.entity._links[rel].href,
                    params: arrayItem.params
                });
            }
        });
    }

    function hasEmbeddedRel (entity, rel) {
        return entity._embedded && entity._embedded.hasOwnProperty(rel);
    }
};