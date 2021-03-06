/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.processing.traverse.content;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

@Dependent
public final class ViewTraverseProcessorImpl extends AbstractContentTraverseProcessor<View<?>, Node<View, Edge>, Edge<View<?>, Node>, ContentTraverseCallback<View<?>, Node<View, Edge>, Edge<View<?>, Node>>>
        implements ViewTraverseProcessor {

    @Inject
    public ViewTraverseProcessorImpl(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        super(treeWalkTraverseProcessor);
        treeWalkTraverseProcessor.useStartNodePredicate(node -> !node.getInEdges().stream()
                .filter(e -> e.getContent() instanceof View)
                .findAny()
                .isPresent());
    }

    @Override
    protected boolean accepts(final Edge edge) {
        return edge.getContent() instanceof View;
    }
}
