package com.example.vtkdemo;

import com.example.vtkdemo.controller.PipelineController;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PipelineController.class)
public class PipelineControllerTest {

    @Autowired
    private MockMvc mvc;
    private String pipeline;

    @Before
    public void setUp() {
        pipeline = "{\n" +
                "   \"studyId\":\"1.3.12.2.1107.5.1.4.79090.30000019050611371988300000022\",\n" +
                "   \"serieId\":\"1.3.12.2.1107.5.1.4.79090.30000019050611410881700005482\",\n" +
                "   \"filters\":[\n" +
                "      {\n" +
                "         \"filterClass\":\"org.itk.simple.RegionOfInterestImageFilter\",\n" +
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
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"org.itk.simple.IntensityWindowingImageFilter\",\n" +
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
                "         \"filterClass\":\"org.itk.simple.ThresholdImageFilter\",\n" +
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
                "         \"filterClass\":\"org.itk.simple.GrayscaleFillholeImageFilter\",\n" +
                "         \"methods\":[\n" +
                "            {\n" +
                "               \"name\":\"fullyConnectedOn\",\n" +
                "               \"parameters\":[]\n" +
                "            }\n" +
                "         ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"filterClass\":\"org.itk.simple.MedianImageFilter\",\n" +
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
                "         \"filterClass\":\"org.itk.simple.BinaryThresholdImageFilter\",\n" +
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
            .andExpect(content().bytes(IOUtils.toByteArray(this.getClass().getResourceAsStream("/test.vtk"))))
            .andExpect(header().string("Vtk-demo-errors", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
