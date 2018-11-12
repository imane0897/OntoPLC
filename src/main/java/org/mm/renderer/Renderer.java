package org.mm.renderer;

import java.util.Optional;

import org.mm.parser.node.MMExpressionNode;
import org.mm.rendering.Rendering;

public interface Renderer {
    Optional<? extends Rendering> render(MMExpressionNode node) throws RendererException;

    public ReferenceRendererConfiguration getReferenceRendererConfiguration();
}
