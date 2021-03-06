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

package org.kie.workbench.common.stunner.cm.client.canvas;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasPresenterTest {

    @Spy
    private Event<CanvasClearEvent> canvasClearEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasShapeAddedEvent> canvasShapeAddedEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasDrawnEvent> canvasDrawnEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasFocusedEvent> canvasFocusedEvent = new EventSourceMock<>();

    @Mock
    private Layer layer;

    @Mock
    private LienzoPanel lienzoPanel;

    @Mock
    private Shape parent;

    @Mock
    private ShapeView parentView;

    @Mock
    private Shape child;

    @Mock
    private ShapeView childView;

    @Mock
    private CaseManagementCanvasView view;

    private CaseManagementCanvasPresenter presenter;

    @Before
    public void setup() {
        when(parent.getShapeView()).thenReturn(parentView);
        when(child.getShapeView()).thenReturn(childView);

        when(view.getWiresManager()).thenReturn(mock(WiresManager.class));

        this.presenter = new CaseManagementCanvasPresenter(canvasClearEvent,
                                                           canvasShapeAddedEvent,
                                                           canvasShapeRemovedEvent,
                                                           canvasDrawnEvent,
                                                           canvasFocusedEvent,
                                                           layer,
                                                           view,
                                                           lienzoPanel);
    }

    @Test
    public void addChildShapeToCaseManagementCanvasView() {
        presenter.addChildShape(parent,
                                child,
                                1);

        verify(view,
               times(1)).addChildShape(eq(parentView),
                                       eq(childView),
                                       eq(1));
    }
}
