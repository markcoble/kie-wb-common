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

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.AnimationShapeStateHelper;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ContainerShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;

public class BasicContainerShape<W, D extends MutableShapeDef<W>, V extends ShapeView<?>>
        extends ContainerShape<W, D, V, Shape<?>> {

    public BasicContainerShape(final D shapeDef,
                               final V view) {
        super(shapeDef,
              view,
              new AnimationShapeStateHelper<>());
    }
}
