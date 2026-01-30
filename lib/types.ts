export type UserRole = 'student' | 'teacher' | 'admin';

export interface User {
  id: string;
  email: string;
  full_name: string;
  role: UserRole;
  avatar_url?: string;
  phone?: string;
  bio?: string;
  department_id?: string;
  created_at: string;
  updated_at: string;
}

export interface Subject {
  id: string;
  title: string;
  code: string;
  description?: string;
  department_id?: string;
  semester_id?: string;
  teacher_id?: string;
  credits: number;
  created_at: string;
}

export interface Assignment {
  id: string;
  subject_id: string;
  title: string;
  description: string;
  instructions?: string;
  due_date: string;
  max_points: number;
  created_by?: string;
  created_at: string;
}

export interface Submission {
  id: string;
  assignment_id: string;
  student_id: string;
  content?: string;
  file_url?: string;
  submitted_at?: string;
  status: 'draft' | 'submitted' | 'graded';
  grade?: number;
  feedback?: string;
}

export interface Enrollment {
  id: string;
  student_id: string;
  subject_id: string;
  enrolled_at: string;
  grade?: number;
}

export interface Notification {
  id: string;
  user_id: string;
  title: string;
  message: string;
  type: 'info' | 'warning' | 'success' | 'error';
  is_read: boolean;
  created_at: string;
}

export interface Message {
  id: string;
  sender_id: string;
  receiver_id: string;
  subject: string;
  content: string;
  is_read: boolean;
  created_at: string;
}