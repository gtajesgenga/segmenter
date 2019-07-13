package com.example.vtkdemo;

import com.example.vtkdemo.controller.PipelineController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PipelineController.class)
public class PipelineControllerTest {

    @Autowired
    private MockMvc mvc;
    private String pipeline;

    @Before
    public void setUp() {
        pipeline = "{\n" +
                "   \"inputPath\":\"/home/gustavo/Descargas/35380361 STRELLA NICOLAS/Columna COL_LUMBAR Adulto/CT COL LUMBAR HUESO\",\n" +
                "   \"outputFile\":\"/home/gustavo/Descargas/35380361 STRELLA NICOLAS/Columna COL_LUMBAR Adulto/CT COL LUMBAR HUESO/out.vtk\",\n" +
                "   \"filters\":[\n" +
                "      {\n" +
                "         \"filterClass\":\"IntensityWindowingImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"setWindowMinimum\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\":-135,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setWindowMaximum\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\":215,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setOutputMinimum\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\":-1502,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setOutputMaximum\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\":1370,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"ThresholdImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"setLower\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\":216.74,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setUpper\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\":1645.76,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setOutsideValue\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": 0.0,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"GrayscaleFillholeImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"fullyConnectedOn\",\n" +
                "               \"parameters\":[]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"MedianImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"setRadius\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                      \"value\": 3,\n" +
                "                      \"casting\": \"java.lang.Long\"\n" +
                "                  }\n" +
                "                ]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"BinaryThresholdImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"setLowerThreshold\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": 1300,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setUpperThreshold\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": 1370,\n" +
                "                     \"casting\": \"java.lang.Double\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setInsideValue\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": 255,\n" +
                "                     \"casting\": \"java.lang.Short\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"name\":\"setOutsideValue\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": 0,\n" +
                "                     \"casting\": \"java.lang.Short\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"RegionOfInterestImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"setSize\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": \"[207,162,132]\",\n" +
                "                     \"casting\": \"org.itk.simple.VectorUInt32\",\n" +
                "                     \"multidimensional\": \"java.lang.Long\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            },\n" +
                "                        {\n" +
                "               \"name\":\"setIndex\",\n" +
                "               \"parameters\":[\n" +
                "                  {\n" +
                "                     \"value\": \"[186,110,380]\",\n" +
                "                     \"casting\": \"org.itk.simple.VectorInt32\",\n" +
                "                     \"multidimensional\": \"java.lang.Integer\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ]\n" +
                "}";
    }

    @Test
    public void testPostMethod() {
        try {
            mvc.perform(post("/pipeline")
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(pipeline)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(content().bytes(new byte[]{}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
