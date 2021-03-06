/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.Magnet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.impl.RuleContextBuilder;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command to set the outgoing connection for an edge.
 * Notes:
 * - In case <code>sourceNode</code> is <code>null</code>, connector's source node, if any, will be removed.
 * - if connector is not view based, no need to provide magnet index.
 */
@Portable
public final class SetConnectionSourceNodeCommand extends AbstractGraphCommand {

    private final String sourceNodeUUID;
    private final String edgeUUID;
    private final Magnet magnet;
    // true if a new connection is being made; false if a different magnet is being selected
    // on an existing connection
    private final boolean isNewConnection;

    private String lastSourceNodeUUID;
    private Magnet lastMagnet;
    private transient Edge<? extends View, Node> edge;
    private transient Node<? extends View<?>, Edge> targetNode;
    private transient Node<? extends View<?>, Edge> sourceNode;

    @SuppressWarnings("unchecked")
    public SetConnectionSourceNodeCommand(final @MapsTo("sourceNodeUUID") String sourceNodeUUID,
                                          final @MapsTo("edgeUUID") String edgeUUID,
                                          final @MapsTo("magnet") Magnet magnet,
                                          final @MapsTo("isNewConnection") boolean isNewConnection) {
        this.edgeUUID = PortablePreconditions.checkNotNull("edgeUUID",
                                                           edgeUUID);
        this.sourceNodeUUID = sourceNodeUUID;
        this.magnet = magnet;
        this.isNewConnection = isNewConnection;
        this.lastSourceNodeUUID = null;
        this.lastMagnet = null;
    }

    @SuppressWarnings("unchecked")
    public SetConnectionSourceNodeCommand(final Node<? extends View<?>, Edge> sourceNode,
                                          final Edge<? extends View, Node> edge,
                                          final Magnet magnet,
                                          final boolean isNewConnection) {
        this(null != sourceNode ? sourceNode.getUUID() : null,
             edge.getUUID(),
             magnet,
             isNewConnection);
        this.sourceNode = sourceNode;
        this.edge = edge;
        this.targetNode = edge.getTargetNode();
    }

    @SuppressWarnings("unchecked")
    public SetConnectionSourceNodeCommand(final Node<? extends View<?>, Edge> sourceNode,
                                          final Edge<? extends View, Node> edge) {
        this(sourceNode,
             edge,
             null,
             true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Node<?, Edge> sourceNode = getSourceNode(context);
            final Edge<? extends View, Node> edge = getEdge(context);
            final Node<? extends View<?>, Edge> lastSourceNode = edge.getSourceNode();
            if (isNewConnection) {
                // New connection being made
                if (null != lastSourceNode) {
                    this.lastSourceNodeUUID = lastSourceNode.getUUID();
                    lastSourceNode.getOutEdges().remove(edge);
                }
                if (null != sourceNode) {
                    sourceNode.getOutEdges().add(edge);
                }
                edge.setSourceNode(sourceNode);
                if (null != magnet) {
                    ViewConnector connectionContent = (ViewConnector) edge.getContent();
                    lastMagnet = (Magnet) connectionContent.getSourceMagnet().orElse(null);
                    connectionContent.setSourceMagnet(magnet);
                }
            }
            else {
                // Magnet being moved on node
                ViewConnector connectionContent = (ViewConnector) edge.getContent();
                lastMagnet = (Magnet) connectionContent.getSourceMagnet().orElse(null);
                connectionContent.setSourceMagnet(magnet);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) getSourceNode(context);
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) getEdge(context);
        final GraphCommandResultBuilder resultBuilder = new GraphCommandResultBuilder();
        if (isNewConnection) {
            // New connection being made
            final Collection<RuleViolation> connectionRuleViolations =
                    doEvaluate(context,
                               RuleContextBuilder.GraphContexts.connection(getGraph(context),
                                                                           edge,
                                                                           Optional.ofNullable(sourceNode),
                                                                           Optional.ofNullable(targetNode)));
            resultBuilder.addViolations(connectionRuleViolations);
            final Node<View<?>, Edge> currentSource = edge.getSourceNode();
            // If the edge has an outoutgoing source node, check cardinality for removing it.
            if (null != currentSource) {
                final Collection<RuleViolation> cardinalityRuleViolations =
                        doEvaluate(context,
                                   RuleContextBuilder.GraphContexts.edgeCardinality(getGraph(context),
                                                                                    currentSource,
                                                                                    edge,
                                                                                    EdgeCardinalityContext.Direction.OUTGOING,
                                                                                    Optional.of(CardinalityContext.Operation.DELETE)));
                resultBuilder.addViolations(cardinalityRuleViolations);
            }
            // If the new source node exist, evaluate cardinality rules for this edge.
            if (null != sourceNode) {
                final Collection<RuleViolation> cardinalityRuleViolations =
                        doEvaluate(context,
                                   RuleContextBuilder.GraphContexts.edgeCardinality(getGraph(context),
                                                                                    sourceNode,
                                                                                    edge,
                                                                                    EdgeCardinalityContext.Direction.OUTGOING,
                                                                                    Optional.of(CardinalityContext.Operation.ADD)));
                resultBuilder.addViolations(cardinalityRuleViolations);
            }
        }
        return resultBuilder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SetConnectionSourceNodeCommand undoCommand = new SetConnectionSourceNodeCommand((Node<? extends View<?>, Edge>) getNode(context,
                                                                                                                                      lastSourceNodeUUID),
                                                                                              getEdge(context),
                                                                                              lastMagnet,
                                                                                              isNewConnection);
        return undoCommand.execute(context);
    }

    @SuppressWarnings("unchecked")
    public Node<? extends View<?>, Edge> getTargetNode(final GraphCommandExecutionContext context) {
        if (null == targetNode) {
            targetNode = getEdge(context).getTargetNode();
        }
        return targetNode;
    }

    @SuppressWarnings("unchecked")
    public Node<? extends View<?>, Edge> getSourceNode(final GraphCommandExecutionContext context) {
        if (null == sourceNode) {
            sourceNode = (Node<? extends View<?>, Edge>) getNode(context,
                                                                 sourceNodeUUID);
        }
        return sourceNode;
    }

    public Edge<? extends View, Node> getEdge(final GraphCommandExecutionContext context) {
        if (null == this.edge) {
            this.edge = getViewEdge(context,
                                    edgeUUID);
        }
        return this.edge;
    }

    public Node<? extends View<?>, Edge> getSourceNode() {
        return sourceNode;
    }

    public Edge<? extends View, Node> getEdge() {
        return edge;
    }

    public Node<? extends View<?>, Edge> getTargetNode() {
        return targetNode;
    }

    public Magnet getMagnet() {
        return magnet;
    }

    public Magnet getLastSourceMagnet() {
        return lastMagnet;
    }

    @Override
    public String toString() {
        return "SetConnectionSourceNodeCommand [edge=" + edgeUUID
                + ", candidate=" + (null != sourceNodeUUID ? sourceNodeUUID : "null")
                + ", magnet=" + magnet + "]";
    }
}
