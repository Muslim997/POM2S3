import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL!;
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!;

export const supabase = createClient(supabaseUrl, supabaseAnonKey);

export type UserRole = 'student' | 'teacher' | 'admin';

export interface Profile {
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
  file_url?: string;
  content?: string;
  status: 'draft' | 'submitted' | 'graded';
  version: number;
  submitted_at?: string;
  created_at: string;
}

export interface Notification {
  id: string;
  user_id: string;
  type: 'assignment_created' | 'assignment_graded' | 'new_message' | 'deadline_approaching' | 'announcement';
  title: string;
  message: string;
  link?: string;
  is_read: boolean;
  created_at: string;
}
