import { NextRequest, NextResponse } from 'next/server';
import { pool } from '@/lib/db';
import jwt from 'jsonwebtoken';

export async function GET(request: NextRequest) {
  try {
    const token = request.headers.get('authorization')?.replace('Bearer ', '');

    if (!token) {
      return NextResponse.json({ error: 'Token manquant' }, { status: 401 });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET!) as any;
    const userId = decoded.id;
    const userRole = decoded.role;

    if (userRole === 'student') {
      // Statistiques pour étudiant
      const [enrollmentsResult] = await pool.execute(
        'SELECT COUNT(*) as count FROM enrollments WHERE student_id = ?',
        [userId]
      );

      const [submissionsResult] = await pool.execute(
        'SELECT COUNT(*) as count FROM submissions WHERE student_id = ? AND status = "draft"',
        [userId]
      );

      const [notificationsResult] = await pool.execute(
        'SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = false',
        [userId]
      );

      return NextResponse.json({
        courses: enrollmentsResult[0].count,
        assignments: submissionsResult[0].count,
        notifications: notificationsResult[0].count,
        grade: '15.5/20',
      });
    } else if (userRole === 'teacher') {
      // Statistiques pour enseignant
      const [subjectsResult] = await pool.execute(
        'SELECT COUNT(*) as count FROM subjects WHERE teacher_id = ?',
        [userId]
      );

      const [assignmentsResult] = await pool.execute(
        'SELECT COUNT(*) as count FROM assignments WHERE teacher_id = ?',
        [userId]
      );

      const [submissionsResult] = await pool.execute(
        'SELECT COUNT(*) as count FROM submissions s JOIN assignments a ON s.assignment_id = a.id WHERE a.teacher_id = ? AND s.status = "submitted"',
        [userId]
      );

      return NextResponse.json({
        courses: subjectsResult[0].count,
        assignments: assignmentsResult[0].count,
        submissions: submissionsResult[0].count,
        notifications: 0,
      });
    } else if (userRole === 'admin') {
      // Statistiques pour admin
      const [usersResult] = await pool.execute('SELECT COUNT(*) as count FROM users');
      const [coursesResult] = await pool.execute('SELECT COUNT(*) as count FROM subjects');
      const [studentsResult] = await pool.execute('SELECT COUNT(*) as count FROM users WHERE role = "student"');
      const [teachersResult] = await pool.execute('SELECT COUNT(*) as count FROM users WHERE role = "teacher"');

      return NextResponse.json({
        users: usersResult[0].count,
        courses: coursesResult[0].count,
        students: studentsResult[0].count,
        teachers: teachersResult[0].count,
      });
    }

    return NextResponse.json({ error: 'Rôle non reconnu' }, { status: 400 });
  } catch (error) {
    console.error('Erreur lors de la récupération des statistiques:', error);
    return NextResponse.json({ error: 'Erreur serveur' }, { status: 500 });
  }
}