package com.example.application.draggable.node;
import com.example.application.DraggableNode;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;


public class DragResize {

    /**
     * Enum containing the zones that we can drag around.
     */
    enum Zone {
        NONE, N, NE, E, SE, S, SW, W, NW, C
    }

    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 5;

    /**
     * How small can we go?
     */
    private static final int MIN_SIZE = 10;

    private final DraggableNode node;

    private double y;

    private double x;

    private boolean initMinHeight;

    private boolean initMinWidth;

    private Zone zone;

    private boolean dragging;

    /**
     * Whether the sizing and movement of the region is constrained within the
     * bounds of the parent.
     */
    private final boolean constrainToParent;

    /**
     * Whether dragging of the region is allowed.
     */
    private final boolean allowMove;

    private DragResize(DraggableNode aNode, boolean allowMove, boolean constrainToParent) {
        node = aNode;
        this.constrainToParent = constrainToParent;
        this.allowMove = allowMove;
    }

    /**
     * Makes the region resizable, but not moveable or constrained in any way.
     *
     * @param node
     */
    public static void makeResizable(DraggableNode node) {
        DragResize.makeResizable(node, false, false);
    }

    /**
     * Makes the region resizable, and optionally moveable, and constrained
     * within the bounds of the parent.
     *
     * @param node
     * @param allowMove Allow a click in the centre of the region to start
     * dragging it around.
     * @param constrainToParent Prevent movement and/or resizing outside the
     * parent.
     */
    public static void makeResizable(DraggableNode node, boolean allowMove, boolean constrainToParent) {
        final DragResize resizer = new DragResize(node, allowMove, constrainToParent);

        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mousePressed(event);
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseDragged(event);
            }
        });
        node.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseOver(event);
            }
        });
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseReleased(event);
            }
        });
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        node.setCursor(Cursor.CLOSED_HAND);
    }

    protected void mouseOver(MouseEvent event) {

        if (isInDraggableZone(event) || dragging) {
            switch (zone) {
                case N: {
                    node.setCursor(Cursor.N_RESIZE);
                    break;
                }
                case NE: {
                    node.setCursor(Cursor.NE_RESIZE);
                    break;
                }
                case E: {
                    node.setCursor(Cursor.E_RESIZE);
                    break;
                }
                case SE: {
                    node.setCursor(Cursor.SE_RESIZE);
                    break;
                }
                case S: {
                    node.setCursor(Cursor.S_RESIZE);
                    break;
                }
                case SW: {
                    node.setCursor(Cursor.SW_RESIZE);
                    break;
                }
                case W: {
                    node.setCursor(Cursor.W_RESIZE);
                    break;
                }
                case NW: {
                    node.setCursor(Cursor.NW_RESIZE);
                    break;
                }
                case C: {
                    node.setCursor(Cursor.MOVE);
                    break;
                }
            }
        } else {
            node.setCursor(Cursor.CLOSED_HAND);

        }
    }

    protected boolean isInDraggableZone(MouseEvent event) {
        zone = Zone.NONE;

        if ((event.getY() < RESIZE_MARGIN) && (event.getX() < RESIZE_MARGIN)) {
            zone = Zone.NW;
        } else if ((event.getY() < RESIZE_MARGIN) && (event.getX() > (node.getWidth() - RESIZE_MARGIN))) {
            zone = Zone.NE;
        } else if ((event.getY() > (node.getHeight() - RESIZE_MARGIN)) && (event.getX() > (node.getWidth() - RESIZE_MARGIN))) {
            zone = Zone.SE;
        } else if ((event.getY() > (node.getHeight() - RESIZE_MARGIN)) && (event.getX() < RESIZE_MARGIN)) {
            zone = Zone.SW;
        } else if (event.getY() < RESIZE_MARGIN) {
            zone = Zone.N;
        } else if (event.getX() < RESIZE_MARGIN) {
            zone = Zone.W;
        } else if (event.getY() > (node.getHeight() - RESIZE_MARGIN)) {
            zone = Zone.S;
        } else if (event.getX() > (node.getWidth() - RESIZE_MARGIN)) {
            zone = Zone.E;
        } else if (allowMove) {
            zone = Zone.C;
        }

        return !Zone.NONE.equals(zone);

    }

    protected void mouseDragged(MouseEvent event) {
        if (!dragging) {
            return;
        }

        double deltaY = event.getSceneY() - y;
        double deltaX = event.getSceneX() - x;

        double originY = node.getLayoutY();
        double originX = node.getLayoutX();

        double newHeight = node.getMinHeight();
        double newWidth = node.getMinWidth();

        switch (zone) {
            case N: {
                originY += deltaY;
                newHeight -= deltaY;
                break;
            }
            case NE: {
                originY += deltaY;
                newHeight -= deltaY;
                newWidth += deltaX;
                break;
            }
            case E: {
                newWidth += deltaX;
                break;
            }
            case SE: {
                newHeight += deltaY;
                newWidth += deltaX;
                break;
            }
            case S: {
                newHeight += deltaY;
                break;
            }
            case SW: {
                originX += deltaX;
                newHeight += deltaY;
                newWidth -= deltaX;
                break;
            }
            case W: {
                originX += deltaX;
                newWidth -= deltaX;
                break;
            }
            case NW: {
                originY += deltaY;
                originX += deltaX;
                newWidth -= deltaX;
                newHeight -= deltaY;
                break;
            }
            case C: {
                originY += deltaY;
                originX += deltaX;
                break;
            }
        }

        if (constrainToParent) {

            if (originX < 0) {
                if (!Zone.C.equals(zone)) {
                    newWidth -= Math.abs(originX);
                }
                originX = 0;
            }
            if (originY < 0) {
                if (!Zone.C.equals(zone)) {
                    newHeight -= Math.abs(originY);
                }
                originY = 0;
            }

            if (Zone.C.equals(zone)) {
                if ((newHeight + originY) > node.getParent().getBoundsInLocal().getHeight()) {
                    originY = node.getParent().getBoundsInLocal().getHeight() - newHeight;
                }
                if ((newWidth + originX) > node.getParent().getBoundsInLocal().getWidth()) {
                    originX = node.getParent().getBoundsInLocal().getWidth() - newWidth;
                }
            } else {
                if ((newHeight + originY) > node.getParent().getBoundsInLocal().getHeight()) {
                    newHeight = node.getParent().getBoundsInLocal().getHeight() - originY;
                }
                if ((newWidth + originX) > node.getParent().getBoundsInLocal().getWidth()) {
                    newWidth = node.getParent().getBoundsInLocal().getWidth() - originX;
                }
            }
        }
        if (newWidth < MIN_SIZE) {
            newWidth = MIN_SIZE;
        }
        if (newHeight < MIN_SIZE) {
            newHeight = MIN_SIZE;
        }

        if (!Zone.C.equals(zone)) {
            // need to set Pref Height/Width otherwise they act as minima.
            node.setMinHeight(newHeight);
            node.setPrefHeight(newHeight);
            node.setMinWidth(newWidth);
            node.setPrefWidth(newWidth);
            node.resizeNode(newWidth,newHeight);
        }
        node.relocate(originX, originY);

        y = event.getSceneY();
        x = event.getSceneX();

    }

    protected void mousePressed(MouseEvent event) {

        // ignore clicks outside of the draggable margin
        if (!isInDraggableZone(event)) {
            return;
        }

        dragging = true;
        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
            node.setMinHeight(node.getHeight());
            initMinHeight = true;
        }

        y = event.getSceneY();

        if (!initMinWidth) {
            node.setMinWidth(node.getWidth());
            initMinWidth = true;
        }

        x = event.getSceneX();
    }

}
