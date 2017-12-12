package shakram02.ahmed.shapelibrary.gl_internals.memory;

import android.content.Context;

import shakram02.ahmed.shapelibrary.R;
import shakram02.ahmed.shapelibrary.gl_internals.utils.TextResourceReader;

/**
 * Created by ahmed on 12/12/17.
 */

public class SimpleProgram {
    GLProgram program;

    public enum GLProgramVariableType {
        Attribute,
        Uniform,
        VertexMatrix,
    }

    public SimpleProgram(String vertexShaderCode, String fragmentShaderCode) {
        program = new GLProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void setColor(GLProgramVariableType type, float[] values) {

    }

    public void setVertices(GLProgramVariableType type, float[] vertices) {

    }

    public void setMvpMatrix(GLProgramVariableType type, float[] mvpMatrix) {

    }

    private void setAttribute() {
    }
}
