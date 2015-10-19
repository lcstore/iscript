package com.lezo.iscript.mybatis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.lezo.iscript.service.crawler.DaoBaseTest;
import com.lezo.iscript.utils.DBFieldUtils;
import com.lezo.iscript.utils.DaoConfigUtils;

public class DaoConfigBuilder extends DaoBaseTest {

    @Test
    public void testBuildSolrConfig() {
        TableSchemaDao tableSchemaDao = getBean(TableSchemaDao.class);
        String tableName = "T_PRODUCT_STAT";
        List<TableSchemaDto> dtoList = tableSchemaDao.getTableSchemas(tableName);
        String source = "<field name=\"id\" type=\"string\" indexed=\"false\" stored=\"true\"/>";
        String prefix = "st_";
        for (TableSchemaDto dto : dtoList) {
            String destLine = source.replace("id", prefix + DBFieldUtils.field2Param(dto.getField()));
            System.err.println(destLine);
        }
    }

    @Test
    public void testBuildConfig() throws Exception {
        TableSchemaDao tableSchemaDao = getBean(TableSchemaDao.class);
        String tableName = "T_PRODUCT_STAT_HIS";
        String daoName = "ProductStatHisDao";
        List<TableSchemaDto> dtoList = tableSchemaDao.getTableSchemas(tableName);
        String daoQualifyName = "com.lezo.iscript.service.crawler.dao." + daoName;
        int index = daoQualifyName.lastIndexOf('.');
        String daoClassPackage = daoQualifyName.substring(0, index);
        String daoClassName = daoQualifyName.substring(index + 1);
        String dtoClassName = daoClassName.replace("Dao", "Dto");
        String path = "src/main/resources/mybatis/mybatis-mapper-" + daoClassName + ".xml";

        List<String> columnList = toFields(dtoList);
        DaoConfigUtils.createDBConfig(path, tableName, daoClassPackage, dtoClassName, columnList);
        String dtoPackage = daoClassPackage.replaceAll(".dao$", ".dto");
        String dtoTxt = buildDto(dtoPackage, dtoClassName, dtoList);

        File dtoFile = new File("src/main/java", dtoPackage.replace(".", File.separator) + File.separator
                + dtoClassName + ".java");
        File daoFile = new File("src/main/java", daoClassPackage.replace(".", File.separator) + File.separator
                + daoClassName + ".java");
        String servicePackage = daoClassPackage.replaceAll(".dao$", ".service");
        String serviceClsName = daoClassName.replace("Dao", "Service");
        File serviceFile = new File("src/main/java", servicePackage.replace(".", File.separator) + File.separator
                + serviceClsName + ".java");

        FileUtils.writeStringToFile(dtoFile, dtoTxt);
        // FileUtils.writeStringToFile(daoFile, buildDao(daoClassPackage, daoClassName, dtoPackage, dtoClassName));
        // FileUtils.writeStringToFile(serviceFile, buildService(servicePackage, serviceClsName, dtoPackage,
        // dtoClassName));

        System.out.println("tableName:" + tableName);
        System.out.println("daoClsName:" + daoQualifyName);
        System.out.println("dtoFile:" + dtoFile);
        System.out.println("daoFile:" + daoFile);
        System.out.println("serviceFile:" + serviceFile);
    }

    public String buildDto(String dtoPackage, String dtoClsName, List<TableSchemaDto> dtoList) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + dtoPackage + ";\n");
        sb.append("\n");
        sb.append("import java.util.Date;\n");
        sb.append("\n");
        sb.append("import lombok.Getter;\n");
        sb.append("import lombok.Setter;\n");
        sb.append("\n");
        sb.append("@Getter\n");
        sb.append("@Setter\n");
        sb.append("public class " + dtoClsName + " {\n");
        for (TableSchemaDto dto : dtoList) {
            String fieldLine = toFieldLine(dto);
            sb.append(fieldLine);
        }
        sb.append("\n");
        sb.append("}");
        return sb.toString();
    }

    public String buildService(String servicePackage, String serviceClsName, String dtoPackage, String dtoClsName)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + servicePackage + ";\n");
        sb.append("\n");
        sb.append("import java.util.List;\n");
        sb.append("\n");
        sb.append("import com.lezo.iscript.common.BaseService;\n");
        sb.append("import " + dtoPackage + "." + dtoClsName + ";\n");
        sb.append("\n");
        sb.append("public interface " + serviceClsName + " extends BaseService<" + dtoClsName + "> {\n");
        sb.append("	List<" + dtoClsName + "> getDtoByIds(List<Long> idList);\n");
        sb.append("}\n");
        sb.append("\n");
        return sb.toString();
    }

    public String buildDao(String daoPackage, String daoClsName, String dtoPackage, String dtoClsName) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + daoPackage + ";\n");
        sb.append("\n");
        sb.append("import java.util.List;\n");
        sb.append("\n");
        sb.append("import org.apache.ibatis.annotations.Param;\n");
        sb.append("\n");
        sb.append("import com.lezo.iscript.common.BaseDao;\n");
        sb.append("import " + dtoPackage + "." + dtoClsName + ";\n");
        sb.append("\n");
        sb.append("public interface " + daoClsName + " extends BaseDao<" + dtoClsName + "> {\n");
        sb.append("\n");
        sb.append("\n");
        sb.append("}\n");
        sb.append("");
        return sb.toString();
    }

    private String toFieldLine(TableSchemaDto dto) {
        StringBuilder sb = new StringBuilder();
        String type = dto.getType().toLowerCase();
        sb.append("	private ");
        if (type.startsWith("int") || type.startsWith("tinyint")) {
            sb.append("Integer");
        } else if (type.startsWith("bigint")) {
            sb.append("Long");
        } else if (type.startsWith("float")) {
            sb.append("Float");
        } else if (type.startsWith("double")) {
            sb.append("Double");
        } else if (type.startsWith("char") || type.startsWith("varchar") || type.startsWith("nvarchar")) {
            sb.append("String");
        } else if (type.startsWith("timestamp") || type.startsWith("datetime")) {
            sb.append("Date");
        }
        List<String> fieldList = new ArrayList<String>(1);
        fieldList.add(dto.getField());
        fieldList = DBFieldUtils.field2Param(fieldList);
        sb.append(" " + fieldList.get(0));
        sb.append(";\n");
        return sb.toString();
    }

    private List<String> toFields(List<TableSchemaDto> dtoList) {
        List<String> fieldList = new ArrayList<String>();
        for (TableSchemaDto dto : dtoList) {
            fieldList.add(dto.getField());
        }
        return fieldList;
    }
}
