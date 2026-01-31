import { NextRequest, NextResponse } from 'next/server';
import { pool } from '@/lib/db';
import jwt from 'jsonwebtoken';

export const dynamic = 'force-dynamic';

export async function GET(request: NextRequest) {
  try {
    const token = request.headers.get('authorization')?.replace('Bearer ', '');

    if (!token) {
      return NextResponse.json({ error: 'Token manquant' }, { status: 401 });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET!) as any;
    const userId = decoded.id;
    const userRole = decoded.role;

    let courses: any[] = [];

    if (userRole === 'student') {
      const [rows] = await pool.execute(`
        SELECT s.*, p.full_name as teacher_name
        FROM subjects s
        JOIN enrollments e ON s.id = e.subject_id
        JOIN users p ON s.teacher_id = p.id
        WHERE e.student_id = ?
      `, [userId]);
      courses = rows;
    } else if (userRole === 'teacher') {
      const [rows] = await pool.execute(`
        SELECT s.*, p.full_name as teacher_name
        FROM subjects s
        JOIN users p ON s.teacher_id = p.id
        WHERE s.teacher_id = ?
      `, [userId]);
      courses = rows;
    } else if (userRole === 'admin') {
      const [rows] = await pool.execute(`
        SELECT s.*, p.full_name as teacher_name
        FROM subjects s
        JOIN users p ON s.teacher_id = p.id
      `);
      courses = rows;
    }

    return NextResponse.json(courses);
  } catch (error) {
    console.error('Erreur lors de la récupération des cours:', error);
    return NextResponse.json({ error: 'Erreur serveur' }, { status: 500 });
  }
}