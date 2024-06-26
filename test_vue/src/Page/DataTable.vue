<template>
  <div class="detail-container">
    <h2>表格名称：{{ tableName }}</h2>
    <div class="table-wrapper">
      <div class="table-container">
        <el-table
          :data="tableData"
          style="width: 800px; margin-top: 20px;"
          :height="400"
          border
        >
          <el-table-column
            v-for="column in sortedColumns"
            :key="column.COLUMN_NAME"
            :prop="column.COLUMN_NAME"
            :label="column.COLUMN_NAME"
            width="column.width"
            align="center"
            :show-overflow-tooltip="true"
          >
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="button-group">
      <el-button type="primary" @click="editTable">编辑</el-button>
      <el-button @click="showAddRowDialog">新增数据</el-button>
      <el-button @click="showFilterDialog">筛选</el-button>
      <el-button @click="cancelFilter">取消筛选</el-button>
      <el-button @click="goBack">返回</el-button>
    </div>
    <el-dialog title="新增数据" :visible.sync="addRowDialogVisible">
      <el-form :model="newRow">
        <el-form-item v-for="column in sortedColumns" :key="column.COLUMN_NAME" :label="column.COLUMN_NAME">
          <el-input v-model="newRow[column.COLUMN_NAME]"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addRowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNewRow">提交</el-button>
      </div>
    </el-dialog>
    <el-dialog title="筛选表数据" :visible.sync="filterDialogVisible">
      <el-form :model="filterForm">
        <el-form-item label="列名称">
          <el-input v-model="filterForm.columnName"></el-input>
        </el-form-item>
        <el-form-item label="最小值">
          <el-input v-model="filterForm.minValue"></el-input>
        </el-form-item>
        <el-form-item label="最大值">
          <el-input v-model="filterForm.maxValue"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="filterDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="applyFilter">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    tableName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      tableData: [
  { id: 1, name: 'Alice', age: 20, email: 'alice@example.com' },
  { id: 2, name: 'Bob', age: 30, email: 'bob@example.com' },
  { id: 3, name: 'Charlie', age: 35, email: 'charlie@example.com' },
  { id: 4, name: 'David', age: 25, email: 'david@example.com' },
  { id: 5, name: 'Eve', age: 28, email: 'eve@example.com' },
  { id: 1, name: 'Alice', age: 20, email: 'alice@example.com' },
  { id: 2, name: 'Bob', age: 30, email: 'bob@example.com' },
  { id: 3, name: 'Charlie', age: 35, email: 'charlie@example.com' },
  { id: 4, name: 'David', age: 25, email: 'david@example.com' },
  { id: 5, name: 'Eve', age: 28, email: 'eve@example.com' }
]
,
columns: [
  { COLUMN_NAME: 'id', COLUMN_KEY: '', width: 60 },
  { COLUMN_NAME: 'name', COLUMN_KEY: 'PRI', width: 100 },
  { COLUMN_NAME: 'age', COLUMN_KEY: '', width: 60 },
  { COLUMN_NAME: 'email', COLUMN_KEY: '', width: 200 }
]
,
      filterDialogVisible: false,
      addRowDialogVisible: false,
      filterForm: {
        columnName: '',
        minValue: '',
        maxValue: ''
      },
      newRow: {}
    };
  },
  computed: {
  sortedColumns() {
    // 创建 columns 数组的副本
    let columnsCopy = [...this.columns];
    // 对副本进行排序
    return columnsCopy.sort((a, b) => {
      if (a.COLUMN_KEY === 'PRI') return -1;
      if (b.COLUMN_KEY === 'PRI') return 1;
      return 0;
    });
  }
},
  methods: {
    fetchColumnData() {
      this.$axios.get(`/modify_database/table_info?tableName=${this.tableName}`)
        .then(response => {
          this.columns = response.data;
          this.fetchTableData();
        })
        .catch(error => {
          console.error('获取列信息失败:', error);
        });
    },
    fetchTableData() {
      this.$axios.get(`/modify_database/table_data?tableName=${this.tableName}`)
        .then(response => {
          this.tableData = response.data;
        })
        .catch(error => {
          console.error('获取表格数据失败:', error);
        });
    },
    showAddRowDialog() {
      // 初始化 newRow 对象
      this.newRow = {};
      this.sortedColumns.forEach(column => {
        this.$set(this.newRow, column.COLUMN_NAME, '');
      });
      this.addRowDialogVisible = true;
    },
    editTable() {
      this.$router.push({ name: 'EditTable', params: { tableName: this.tableName } });
    },
    goBack() {
      this.$router.push({ name: 'SqlMainpage' });
    },
    showFilterDialog() {
      this.filterDialogVisible = true;
    },
    applyFilter() {
      const { columnName, minValue, maxValue } = this.filterForm;
      this.$axios.post('/modify_database/filter', {
        params: {
          tableName: this.tableName,
          columnName,
          minValue,
          maxValue
        }
      })
      .then(response => {
        this.tableData = response.data;
        this.filterDialogVisible = false;
      })
      .catch(error => {
        console.error('筛选表格数据失败:', error);
      });
    },
    cancelFilter() {
      this.fetchTableData();
    },
    submitNewRow() {
      // 将新行数据添加到 tableData 中
      const payload = {
        name: this.tableName,
        row: this.newRow
      };
      console.log('Payload:', payload);
      this.$axios.post('/modify_database/insert', payload)
        .then(response => {
          console.log('提交成功:', response.data);
          this.$message.success('提交成功');
          this.tableData.push({...this.newRow} );
        })
        .catch(error => {
          console.error('提交失败:', error);
          this.$message.error('提交失败');
        });
      this.addRowDialogVisible = false;
    }
  },
  created() {
    this.fetchColumnData();
  }
};
</script>

<style scoped>
.table-wrapper {
  overflow: auto;
  width: 100%;
  display: flex;
  justify-content: center;
  max-height: calc(100vh - 100px);
}

.table-container {
  width: fit-content;
}

.button-group {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  max-width: 800px;
  margin: 20px auto;
}

.el-table {
  margin-top: 20px;
}

.el-button {
  margin-right: 10px;
}
.detail-container {
  height: calc(100vh - 40px);
  padding: 20px;
  box-sizing: border-box;
}
</style>
