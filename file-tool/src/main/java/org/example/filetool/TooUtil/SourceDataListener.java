package org.example.filetool.TooUtil;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

public class SourceDataListener extends AnalysisEventListener<SourceData> {
    private final List<SourceData> dataList = new ArrayList<>();

    @Override
    public void invoke(SourceData data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 读取完成后的操作
    }

    public List<SourceData> getDataList() {
        return dataList;
    }
}