<template>
  <div class="common-layout">
    <el-container>
      <el-header class="top-bar">
        <el-row>

          <el-col :span="8">
            <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
              <span class="text-xl font-bold">聪明教务系统</span>
            </div>
          </el-col>

          <el-col :span="8" :offset="8">
            <div class="flex items-center justify-end h-full">
              <div style="display: flex; align-items: center; justify-content: center; height: 100%;">
                <el-avatar :size="32" class="mr-4"
                  src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <span class="text-lg font-semibold mr-5" style="margin-left: 20px ; margin-right: 10px;">{{
            this.userName }}</span>
                <span class="text-sm mr-4"
                  style="color: var(--el-text-color-regular); position: relative; top: 2px;margin-right: 10px;">{{
            this.userId }}</span>
                <el-tag type="success" class="ml-2">管理员</el-tag>
              </div>
            </div>
          </el-col>

        </el-row>
      </el-header>

      <div class="main-content">

        <el-aside width="200px">
          <el-menu default-active="1" class="el-menu-vertical-demo">
            <el-menu-item index="2" @click="selectFunction('账号管理')">账号管理</el-menu-item>
          </el-menu>
        </el-aside>

        <div class="main-content-right">

          <div v-if="selectedFunction === '账号管理'">
            <el-table max-height="500" :data="myCourses" style="width: 100%">
              <el-table-column prop="id" label="ID" />
              <el-table-column prop="name" label="姓名" />
              <el-table-column prop="role" label="角色" />
              <el-table-column align="right">
                <template #header>
                  <!-- <div style="display: flex; align-items: center;"> -->
                    <!-- <el-input v-model="search" size="small" placeholder="Type to search" /> -->
                    <el-button primary @click="dialogFormVisible = true">
                      新建用户
                    </el-button>
                  <!-- </div> -->
                </template>
                <template #default="scope">
                  <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
                    编辑
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleDelete(scope.$index, scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </el-container>
    <el-dialog v-model="dialogFormVisible" :title="editFlag ? '编辑用户' : '添加用户'" width="500">
      <el-form>
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth">
          <el-input password v-model="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="身份" :label-width="formLabelWidth">
          <el-select v-model="role" placeholder="身份">
            <el-option label="老师" value="2" />
            <el-option label="学生" value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd">
            {{ editFlag ? '更新' : '添加' }}
          </el-button>          
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script>
import axios from "axios";
import { ElMessage } from 'element-plus'

export default {
  name: "TeacherPages",
  components: {

  },

  // 来自父组件的数据
  props: {
  },

  // 在created生命周期钩子中访问路由参数
  created() {
    console.log("this.$route", this.$route);
    this.userId = this.$route.params.userId;
    this.userName = this.$route.params.userName;
    console.log("userId", this.userId);
    console.log("userName", this.userName);

    this.fetchCourses();
  },

  // data()函数部分
  data() {
    return {
      host: "http://127.0.0.1:9000",
      selectedFunction: "账号管理", // 默认选中的功能
      myCourses: [],
      total: 50,
      dialogFormVisible: false,
      formLabelWidth: "140px",
      editFlag: false,
      name: "",
      password: "",
      role: "",
      id: ""
    };
  },

  watch: {
    selectedCourse() {
      // 调用 fetchStudents 方法，更新 tableData 数组中的数据
      this.fetchStudents();
    }
  },

  methods: {
    async handleDelete(index, row) {
      console.log(index, row);
      const apiUrl = `${this.host}/api/v2/users/deleteUser/${row.id}`;
      console.log("apiUrl", apiUrl);
      try {
        // 发送 GET 请求
        const response = await axios.get(apiUrl);
        if (response.data.code === 200) {
          ElMessage.success(`${row.name}删除成功`);
          this.fetchCourses();
        }

      } catch (error) {
        console.error("删除失败", error);
        ElMessage.error("删除失败");
      }
    },
    handleEdit(index, row) {
      this.editFlag = true
      this.id = row.id
      this.name = row.name
      this.password = ""
      this.role = row.role

      this.dialogFormVisible = true

      const requestBody = {
        id: this.id,
        name: this.name,
        password: this.password,
        roleId: this.role,
        yxh: "7"
      };

      // const apiUrl = `${this.host}/api/v2/users/upsertUser`;
      // console.log("apiUrl", apiUrl);
      // try {
      //   // 发送 GET 请求
      //   const response = await axios.post(apiUrl, requestBody);
      //   if (response.data.code === 200) {
      //     ElMessage.success(`更新成功`);
      //     this.fetchCourses();
      //     this.id = ""
      //     this.name = ""
      //     this.password = ""
      //     this.role = ""
      //   }
      // } catch (error) {
      //   console.error("更新失败", error);
      //   ElMessage.error("更新失败");
      // }

      // this.dialogFormVisible = false
    },
    async handleAdd() {
      const requestBody = {
        id: this.id,
        name: this.name,
        password: this.password,
        roleId: this.role,
        yxh: "7"
      };

      const apiUrl = `${this.host}/api/v2/users/upsertUser`;
      console.log("apiUrl", apiUrl);
      try {
        // 发送 GET 请求
        const response = await axios.post(apiUrl, requestBody);
        if (response.data.code === 200) {
          ElMessage.success(`添加成功`);
          this.fetchCourses();
          this.id = ""
          this.name = ""
          this.password = ""
          this.role = ""
        }

      } catch (error) {
        console.error("添加失败", error);
        ElMessage.error("添加失败");
      }

      this.dialogFormVisible = false
    },
    // 选择功能
    selectFunction(functionName) {
      this.selectedFunction = functionName;
    },

    // 查询该教师已经开设的课程
    async fetchCourses() {
      // 构造请求体 /api/teachers/{userId}/courses
      const apiUrl = `${this.host}/api/v2/users/list?pageNum=1&pageSize=1000`;
      console.log("apiUrl", apiUrl);
      try {
        // 发送 GET 请求
        const response = await axios.get(apiUrl);
        console.log("return from fetchCourses, response: ", response.data);
        this.myCourses = response.data.data;
        console.log("this.myCourses", this.myCourses);

      } catch (error) {
        console.error("课表信息查询失败", error);
        ElMessage.error("课表信息查询失败");
      }
    },

    /* 
    通过watch部分关联，每次完成选择后自动调用 fetchStudents 方法，并更新 tableData 数组中的数据
    查询该教师和他开设的所有课程的信息返回到tableData中
    对应文档中的接口 /教师/课程学生名单：GET /api/teachers/{teacher_id}/courses/{course_id}
    */
    async fetchStudents() {

      // 构造请求体
      const apiUrl = `${this.host}/api/teachers/${this.userId}/courses/${this.selectedCourse}`;
      console.log("this.selectedCourse", this.selectedCourse);

      try {
        // 发送 GET 请求
        const response = await axios.get(apiUrl);
        console.log("return from fetchStudents, response: ", response);

        // 用JSON.parse()方法将字符串转换为JSON对象
        const courseData = response.data;
        this.tableData = courseData.data.map(course => JSON.parse(course));

        console.log("this.tableData", this.tableData);
      }
      catch (error) {
        console.error("该班级下学生信息查询失败", error);
        ElMessage.error("班级下学生信息查询失败");
      }
    },

    // 上传成绩
    async submitScore() {

      // 构造请求体,路径参数传递教师号和课程号
      const apiUrl = `${this.host}/api/teachers/${this.userId}/courses/${this.selectedCourse}`;
      console.log("this.tableData", this.tableData);

      try {
        // 将tableData中的数据转换为SubmitData中的数据
        this.SubmitData = this.tableData.map(student => {
          return {
            student_id: student.student_id,
            daily_score: student.daily_score,
            examination_score: student.examination_score
          }
        });
        console.log("this.SubmitData", this.SubmitData);

        // 发送 POST 请求
        const response = await axios.post(apiUrl, this.SubmitData);

        // 返回状态码为200，表示上传成功
        if (response.data.code === 200) {
          console.log("return from fetchCourses, response:", response);
          ElMessage.success("成绩上传成功");
        }
        else {
          ElMessage.error("成绩上传失败");
        }
      }
      catch (error) {
        console.error("成绩上传失败", error);
        ElMessage.error("成绩上传失败");
      }
    },
  },
};
</script>

<style>
.top-bar {
  background: #208fcb;
  color: #fff;
  padding: 10px 20px;
  text-align: center;
  border-radius: 10px;
}

.top-bar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-name {
  font-weight: bold;
}

.main-content {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
}

.sidebar {
  margin-top: 10px;
  width: 100px;
  background: #aee3ed;
  padding: 10px;
  border-radius: 10px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  cursor: pointer;
  padding: 5px;
  border-bottom: 1px solid #ccc;
}

.main-content-right {
  flex: 1;
  padding: 20px;
}

.input-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

label {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}


.course-table {
  margin-top: 20px;
  margin-bottom: 20px;
  border-collapse: collapse;
  font-family: Arial, sans-serif;
  background-color: #f2f2f2;
  width: 100%;
}

.course-table th,
.course-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.course-table th {
  background-color: #8ac9e2;
  color: white;
}
</style>